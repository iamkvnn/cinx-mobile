package com.app.cinx.activity;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.LinkedWalletAdapter;
import com.app.cinx.model.BankCard;
import com.app.cinx.model.LinkedWallet;
import com.app.cinx.util.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * PaymentMethodsActivity
 *
 * Displays:
 *  1. A FrameLayout-based physical card stack – cards are stacked on top of each other.
 *     Tap the stack or swipe up/down to cycle through cards.
 *  2. Linked e-wallets with an Unlink action.
 *  3. An "Add new card" bottom sheet.
 */
public class PaymentMethodsActivity extends AppCompatActivity
        implements LinkedWalletAdapter.WalletListener {

    // ─────────────────────────────────────────────────────────────────
    // Views
    // ─────────────────────────────────────────────────────────────────

    private ImageView    btnBack;
    private FrameLayout  cardStackFrame;
    private LinearLayout dotsContainer;
    private LinearLayout btnAddCard;
    private RecyclerView rvWallets;
    private LinearLayout btnLinkWallet;

    // Sheet views
    private View     sheetOverlay;
    private View     addCardSheet;
    private View     sheetDragHandle;
    private EditText etCardNumber;
    private EditText etCardholderName;
    private EditText etExpiry;
    private EditText etCvv;
    private TextView btnSaveCard;

    // ─────────────────────────────────────────────────────────────────
    // Card stack state
    // ─────────────────────────────────────────────────────────────────

    private final List<View> cardViews    = new ArrayList<>();
    private int              topCardIndex = 0;   // index in bankCards currently on top
    private float            touchStartY  = 0f;

    private final List<BankCard> bankCards = buildSampleCards();

    // ─────────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        bindViews();
        setupBackButton();
        setupCardStack();
        setupWallets();
        setupAddCard();
        setupSheet();
    }

    // ─────────────────────────────────────────────────────────────────
    // View binding
    // ─────────────────────────────────────────────────────────────────

    private void bindViews() {
        btnBack          = findViewById(R.id.btnBack);
        cardStackFrame   = findViewById(R.id.cardStackFrame);
        dotsContainer    = findViewById(R.id.dotsContainer);
        btnAddCard       = findViewById(R.id.btnAddCard);
        rvWallets        = findViewById(R.id.rvWallets);
        btnLinkWallet    = findViewById(R.id.btnLinkWallet);
        sheetOverlay     = findViewById(R.id.sheetOverlay);
        addCardSheet     = findViewById(R.id.addCardSheet);
        sheetDragHandle  = findViewById(R.id.sheetDragHandle);
        etCardNumber     = findViewById(R.id.etCardNumber);
        etCardholderName = findViewById(R.id.etCardholderName);
        etExpiry         = findViewById(R.id.etExpiry);
        etCvv            = findViewById(R.id.etCvv);
        btnSaveCard      = findViewById(R.id.btnSaveCard);
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());
    }

    // ─────────────────────────────────────────────────────────────────
    // Card stack
    // ─────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────
    // Card stack (physical stack, tap / swipe up-down to cycle)
    // ─────────────────────────────────────────────────────────────────

    private void setupCardStack() {
        cardViews.clear();
        cardStackFrame.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        // CARD_H = 190dp; the 230dp frame gives 40dp room for 2 peek strips below.
        int cardH = dp(190);

        for (int i = 0; i < bankCards.size(); i++) {
            View cardView = inflater.inflate(R.layout.item_bank_card, cardStackFrame, false);
            // Override height to fixed 190dp so peek math is deterministic
            FrameLayout.LayoutParams lp =
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, cardH);
            cardView.setLayoutParams(lp);
            bindCardView(cardView, bankCards.get(i));
            cardStackFrame.addView(cardView);
            cardViews.add(cardView);
        }

        buildDotIndicators();
        renderStack(false);

        // Touch: swipe up → next, swipe down → prev, tap → next.
        // requestDisallowInterceptTouchEvent prevents NestedScrollView from
        // consuming the vertical gesture before cardStackFrame sees it.
        cardStackFrame.setClickable(true);
        cardStackFrame.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    touchStartY = event.getY();
                    // Block NestedScrollView from stealing this gesture
                    if (v.getParent() != null)
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (v.getParent() != null)
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (v.getParent() != null)
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                    float dy = touchStartY - event.getY();
                    if (dy > dp(20)) {
                        cycleToNext();
                    } else if (dy < -dp(20)) {
                        cycleToPrev();
                    } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                        cycleToNext(); // tap
                    }
                    return true;
            }
            return false;
        });
    }

    /** Bind BankCard data to an inflated item_bank_card view. */
    private void bindCardView(View v, BankCard card) {
        android.graphics.drawable.GradientDrawable bg =
                new android.graphics.drawable.GradientDrawable(
                        android.graphics.drawable.GradientDrawable.Orientation.TL_BR,
                        new int[]{ card.getColorStart(), card.getColorEnd() });
        bg.setCornerRadius(dp(20));
        v.findViewById(R.id.cardRoot).setBackground(bg);

        ((TextView) v.findViewById(R.id.tvNetwork)).setText(
                card.getNetwork() == BankCard.Network.VISA ? "VISA" : "MC");
        ((TextView) v.findViewById(R.id.tvCardNumber)).setText(card.getMaskedNumber());
        ((TextView) v.findViewById(R.id.tvCardholderName)).setText(card.getCardholderName());
        ((TextView) v.findViewById(R.id.tvExpiry)).setText(card.getExpiry());
    }

    private void cycleToNext() {
        topCardIndex = (topCardIndex + 1) % bankCards.size();
        renderStack(true);
    }

    private void cycleToPrev() {
        topCardIndex = (topCardIndex - 1 + bankCards.size()) % bankCards.size();
        renderStack(true);
    }

    /**
     * Positions all card views to create the physical stacking effect.
     *
     * Cards are 190dp tall inside a 230dp frame, so:
     *   visPos=0 (front) : translateY=0,    scaleX=1.00  → occupies 0–190dp
     *   visPos=1 (behind): translateY=16dp, scaleX=0.93  → peeks 16dp below front
     *   visPos=2 (behind): translateY=28dp, scaleX=0.86  → peeks 28dp below front
     *   visPos≥3          : hidden (alpha=0)
     *
     * Only scaleX shrinks (card gets narrower, not shorter) so the peek strip
     * is always visible at the correct translated position.
     */
    private void renderStack(boolean animate) {
        int n = cardViews.size();
        // translateY offsets for the peek strip (index = visPos)
        int[] offsets = { 0, dp(16), dp(28) };
        // horizontal scale so behind cards look narrower
        float[] scalesX = { 1.00f, 0.93f, 0.86f };

        for (int i = 0; i < n; i++) {
            int visPos = (i - topCardIndex + n) % n;
            float alpha  = visPos < 3 ? 1f : 0f;
            float transY = visPos < 3 ? offsets[visPos] : dp(28);
            float scaleX = visPos < 3 ? scalesX[visPos] : 0.86f;
            // Z: front card is on top, behind cards go underneath
            float zVal   = (n - visPos) * dp(4);

            View card = cardViews.get(i);
            card.setTranslationZ(zVal);
            if (animate) {
                card.animate()
                        .scaleX(scaleX).scaleY(1f)   // only horizontal scaling
                        .translationY(transY)
                        .alpha(alpha)
                        .setDuration(380)
                        .setInterpolator(new DecelerateInterpolator(1.5f))
                        .start();
            } else {
                card.setScaleX(scaleX);
                card.setScaleY(1f);
                card.setTranslationY(transY);
                card.setAlpha(alpha);
            }
        }
        updateDots(topCardIndex);
    }

    // ── Dot indicators ───────────────────────────────────────────────

    private void buildDotIndicators() {
        dotsContainer.removeAllViews();
        for (int i = 0; i < bankCards.size(); i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(8), dp(8));
            lp.setMargins(dp(3), 0, dp(3), 0);
            dot.setLayoutParams(lp);
            dot.setBackgroundResource(R.drawable.dot_inactive);
            dotsContainer.addView(dot);
            final int idx = i;
            dot.setOnClickListener(v -> { topCardIndex = idx; renderStack(true); });
        }
    }

    private void updateDots(int activePos) {
        for (int i = 0; i < dotsContainer.getChildCount(); i++) {
            View dot = dotsContainer.getChildAt(i);
            if (i == activePos) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(20), dp(8));
                lp.setMargins(dp(3), 0, dp(3), 0);
                dot.setLayoutParams(lp);
                dot.setBackgroundResource(R.drawable.dot_active);
            } else {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(8), dp(8));
                lp.setMargins(dp(3), 0, dp(3), 0);
                dot.setLayoutParams(lp);
                dot.setBackgroundResource(R.drawable.dot_inactive);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Wallets
    // ─────────────────────────────────────────────────────────────────

    private void setupWallets() {
        List<LinkedWallet> wallets = buildSampleWallets();
        LinkedWalletAdapter walletAdapter = new LinkedWalletAdapter(wallets, this);
        rvWallets.setLayoutManager(new LinearLayoutManager(this));
        rvWallets.setAdapter(walletAdapter);
        rvWallets.setNestedScrollingEnabled(false);
    }

    @Override
    public void onUnlink(LinkedWallet wallet) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(getString(R.string.pm_unlink_title, wallet.getDisplayName()))
                .setMessage(R.string.pm_unlink_confirm)
                .setPositiveButton(R.string.pm_unlink_yes, (d, w) ->
                        ToastUtil.showCustomToast(this,
                                getString(R.string.pm_unlinked_toast, wallet.getDisplayName())))
                .setNegativeButton(R.string.pm_unlink_cancel, null)
                .show();
    }

    // ─────────────────────────────────────────────────────────────────
    // Add card sheet
    // ─────────────────────────────────────────────────────────────────

    private void setupAddCard() {
        btnAddCard.setOnClickListener(v -> showSheet());
        btnLinkWallet.setOnClickListener(v ->
                ToastUtil.showCustomToast(this, getString(R.string.pm_link_wallet_coming)));
    }

    private void setupSheet() {
        sheetOverlay.setOnClickListener(v -> hideSheet());
        sheetDragHandle.setOnClickListener(v -> hideSheet());
        btnSaveCard.setOnClickListener(v -> onSaveCard());
    }

    private void showSheet() {
        sheetOverlay.setVisibility(View.VISIBLE);
        sheetOverlay.setAlpha(0f);
        sheetOverlay.animate().alpha(1f).setDuration(250).start();

        addCardSheet.post(() -> {
            float startY = addCardSheet.getHeight();
            ObjectAnimator anim = ObjectAnimator.ofFloat(
                    addCardSheet, "translationY", startY, 0f);
            anim.setDuration(380);
            anim.setInterpolator(new DecelerateInterpolator(1.5f));
            anim.start();
        });
    }

    private void hideSheet() {
        float endY = addCardSheet.getHeight();
        ObjectAnimator anim = ObjectAnimator.ofFloat(
                addCardSheet, "translationY", 0f, endY);
        anim.setDuration(300);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                sheetOverlay.setVisibility(View.GONE);
            }
        });
        sheetOverlay.animate().alpha(0f).setDuration(250).start();
        anim.start();
    }

    private void onSaveCard() {
        String number = etCardNumber.getText().toString().trim();
        String name   = etCardholderName.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();
        String cvv    = etCvv.getText().toString().trim();

        if (number.length() < 16) {
            ToastUtil.showCustomToast(this, getString(R.string.pm_invalid_number));  return;
        }
        if (name.isEmpty()) {
            ToastUtil.showCustomToast(this, getString(R.string.pm_invalid_name));    return;
        }
        if (expiry.length() < 5) {
            ToastUtil.showCustomToast(this, getString(R.string.pm_invalid_expiry));  return;
        }
        if (cvv.length() < 3) {
            ToastUtil.showCustomToast(this, getString(R.string.pm_invalid_cvv));     return;
        }

        // TODO: send to backend / tokenize card
        ToastUtil.showCustomToast(this, getString(R.string.pm_card_saved));
        clearSheetFields();
        hideSheet();
    }

    private void clearSheetFields() {
        etCardNumber    .setText("");
        etCardholderName.setText("");
        etExpiry        .setText("");
        etCvv           .setText("");
    }

    @Override
    public void onBackPressed() {
        if (sheetOverlay.getVisibility() == View.VISIBLE) {
            hideSheet();
        } else {
            super.onBackPressed();
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Sample data
    // ─────────────────────────────────────────────────────────────────

    private List<BankCard> buildSampleCards() {
        return Arrays.asList(
                new BankCard(
                        "card_001",
                        BankCard.Network.VISA,
                        "NGUYEN HOANG MINH",
                        "4281",
                        "12/28",
                        Color.parseColor("#1A1F2E"),
                        Color.parseColor("#2D3561")
                ),
                new BankCard(
                        "card_002",
                        BankCard.Network.MASTERCARD,
                        "NGUYEN HOANG MINH",
                        "9174",
                        "08/27",
                        Color.parseColor("#3B0764"),
                        Color.parseColor("#7C3AED")
                )
        );
    }

    private List<LinkedWallet> buildSampleWallets() {
        return Arrays.asList(
                new LinkedWallet(
                        "wallet_001",
                        LinkedWallet.WalletType.MOMO,
                        "Ví MoMo",
                        "0912***678",
                        R.drawable.ic_pm_momo,
                        Color.parseColor("#AE2070")
                )
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // Utility
    // ─────────────────────────────────────────────────────────────────

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
