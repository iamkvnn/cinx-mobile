import os
import json
import glob
from collections import defaultdict

assets_dir = 'app/src/main/assets'
api_dir = 'app/src/main/java/com/app/cinx/api'
dto_dir = 'app/src/main/java/com/app/cinx/api/dto'

os.makedirs(api_dir, exist_ok=True)
os.makedirs(dto_dir, exist_ok=True)

def resolve_type(prop):
    if '$ref' in prop:
        return prop['$ref'].split('/')[-1]
    if 'type' in prop:
        t = prop['type']
        if t == 'string': return 'String'
        if t == 'integer': return 'Long' if prop.get('format') == 'int64' else 'Integer'
        if t == 'number': return 'Double'
        if t == 'boolean': return 'Boolean'
        if t == 'array':
            if 'items' in prop:
                items = prop['items']
                t_item = resolve_type(items)
                return f"List<{t_item}>"
            return "List<Object>"
    return 'Object'

for file_path in glob.glob(f'{assets_dir}/*.json'):
    with open(file_path, 'r', encoding='utf-8') as f:
        root = json.load(f)

    file_name = os.path.basename(file_path)
    base_name = file_name.replace('.json', '').capitalize()

    # schemas
    if 'components' in root and 'schemas' in root['components']:
        for schema_name, schema in root['components']['schemas'].items():
            if schema_name.startswith('ApiResponse') or schema_name.startswith('ApiListResponse') or schema_name in ('PageMeta', 'ApiResponseObject'):
                continue

            lines = [
                f"package com.app.cinx.api.dto;",
                f"",
                f"import java.util.List;",
                f"",
                f"public class {schema_name} {{"
            ]

            properties = schema.get('properties', {})
            for prop_name, prop in properties.items():
                j_type = resolve_type(prop)
                lines.append(f"    private {j_type} {prop_name};")

                c_name = prop_name[0].upper() + prop_name[1:]
                lines.append(f"    public {j_type} get{c_name}() {{ return {prop_name}; }}")
                lines.append(f"    public void set{c_name}({j_type} val) {{ this.{prop_name} = val; }}")
                lines.append("")

            # mock field preserved for ui consistency
            if schema_name in ('CartItemResponse', 'PaymentMethodResponse', 'VoucherResponse'):
                lines.append(f"    private Boolean isSelected = false;")
                lines.append(f"    public Boolean isSelected() {{ return isSelected != null && isSelected; }}")
                lines.append(f"    public void setSelected(Boolean val) {{ this.isSelected = val; }}")
                lines.append("")

            if schema_name == 'CartItemResponse':
                lines.append(f"    public String getTitle() {{ return course != null ? course.getTitle() : \"\"; }}")
                lines.append(f"    public String getInstructor() {{ return course != null && course.getInstructor() != null ? course.getInstructor().getName() : (course != null ? course.getDescription() : \"\"); }}")
                lines.append(f"    public long getSalePrice() {{ return course != null && course.getDiscountedPrice() != null ? course.getDiscountedPrice() : (course != null && course.getPrice() != null ? course.getPrice() : 0L); }}")
                lines.append(f"    public long getOriginalPrice() {{ return course != null && course.getPrice() != null ? course.getPrice() : 0L; }}")
                lines.append(f"    public String getImageUrl() {{ return \"https://images.unsplash.com/photo-1561070791-2526d30994b5?w=400\"; }}")
                lines.append(f"    public String getCategory() {{ return course != null ? course.getCategory() : \"\"; }}")
                lines.append("")

            lines.append("}")

            with open(os.path.join(dto_dir, f"{schema_name}.java"), 'w', encoding='utf-8') as f:
                f.write("\n".join(lines))

    # paths
    if 'paths' in root:
        api_name = f"{base_name}Service"
        lines = [
            f"package com.app.cinx.api;",
            f"",
            f"import com.app.cinx.api.dto.*;",
            f"import retrofit2.Call;",
            f"import retrofit2.http.*;",
            f"import java.util.List;",
            f"",
            f"public interface {api_name} {{"
        ]

        for path_name, ops in root['paths'].items():
            for method, op in ops.items():
                method_name = op.get('operationId', f"do{method.capitalize()}")
                ret_type = "Object"

                # resolve response
                responses = op.get('responses', {})
                resp_200 = responses.get('200', {})
                if 'content' in resp_200:
                    content = resp_200['content']
                    if '*/*' in content and 'schema' in content['*/*']:
                        ret_type = resolve_type(content['*/*']['schema'])
                    elif 'application/json' in content and 'schema' in content['application/json']:
                        ret_type = resolve_type(content['application/json']['schema'])

                # handle wrappers
                if ret_type.startswith("ApiResponseList"):
                    sub = ret_type.replace("ApiResponseList", "")
                    ret_type = f"ApiResponse<List<{sub}>>"
                elif ret_type.startswith("ApiResponse"):
                    sub = ret_type.replace("ApiResponse", "")
                    if not sub or sub == "Object" or sub == "Void": sub = "Void" if sub == "Void" else "Object"
                    ret_type = f"ApiResponse<{sub}>"
                elif ret_type.startswith("ApiListResponse"):
                    sub = ret_type.replace("ApiListResponse", "")
                    ret_type = f"ApiListResponse<{sub}>"

                lines.append(f"    @{method.upper()}(\"{path_name[1:]}\")")

                params_list = []
                parameters = op.get('parameters', [])
                for p in parameters:
                    p_in = p.get('in')
                    p_name = p.get('name')
                    p_type = resolve_type(p.get('schema', {})) if 'schema' in p else "String"

                    if p_in == 'query': params_list.append(f"@Query(\"{p_name}\") {p_type} {p_name}")
                    if p_in == 'path': params_list.append(f"@Path(\"{p_name}\") {p_type} {p_name}")

                request_body = op.get('requestBody', {})
                if 'content' in request_body and 'application/json' in request_body['content']:
                    body_schema = request_body['content']['application/json'].get('schema', {})
                    body_type = resolve_type(body_schema)
                    params_list.append(f"@Body {body_type} body")

                params_str = ", ".join(params_list)
                lines.append(f"    Call<{ret_type}> {method_name}({params_str});")
                lines.append("")

        lines.append("}")

        with open(os.path.join(api_dir, f"{api_name}.java"), 'w', encoding='utf-8') as f:
            f.write("\n".join(lines))

print("Code generated successfully.")
