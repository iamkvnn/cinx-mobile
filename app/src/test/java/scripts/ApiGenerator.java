package scripts;

import org.junit.Test;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiGenerator {

    @Test
    public void generateApisAndDtos() {
        try {
            File assetsDir = new File("src/main/assets");
            File[] jsonFiles = assetsDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (jsonFiles == null) return;

            Gson gson = new Gson();
            Map<String, String> generatedDtos = new HashMap<>();
            Map<String, String> generatedApis = new HashMap<>();

            for (File file : jsonFiles) {
                System.out.println("Processing " + file.getName());
                JsonObject root = gson.fromJson(new FileReader(file), JsonObject.class);
                if (root.has("components") && root.getAsJsonObject("components").has("schemas")) {
                    JsonObject schemas = root.getAsJsonObject("components").getAsJsonObject("schemas");
                    for (String schemaName : schemas.keySet()) {
                        if (schemaName.startsWith("ApiResponse") || schemaName.startsWith("ApiListResponse") 
                            || schemaName.equals("PageMeta")) {
                            continue;
                        }
                        JsonObject schema = schemas.getAsJsonObject(schemaName);
                        generatedDtos.put(schemaName, generateDtoClass(schemaName, schema));
                    }
                }

                if (root.has("paths")) {
                    String apiName = file.getName().replace(".json", "");
                    apiName = apiName.substring(0, 1).toUpperCase() + apiName.substring(1) + "Service";
                    JsonObject paths = root.getAsJsonObject("paths");
                    generatedApis.put(apiName, generateApiInterface(apiName, paths));
                }
            }

            File dtoDir = new File("src/main/java/com/app/cinx/api/dto");
            if (!dtoDir.exists()) dtoDir.mkdirs();
            for (Map.Entry<String, String> entry : generatedDtos.entrySet()) {
                if (entry.getKey().contains("ApiResponse")) continue;
                try (FileWriter writer = new FileWriter(new File(dtoDir, entry.getKey() + ".java"))) {
                    writer.write(entry.getValue());
                }
            }

            File apiDir = new File("src/main/java/com/app/cinx/api");
            for (Map.Entry<String, String> entry : generatedApis.entrySet()) {
                try (FileWriter writer = new FileWriter(new File(apiDir, entry.getKey() + ".java"))) {
                    writer.write(entry.getValue());
                }
            }
        } catch (Exception e) {
            try {
                java.io.FileWriter fw = new java.io.FileWriter("error.txt");
                java.io.PrintWriter pw = new java.io.PrintWriter(fw);
                e.printStackTrace(pw);
                pw.close();
            } catch(Exception ignored) {}
        }
    }

    private String generateDtoClass(String name, JsonObject schema) {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.app.cinx.api.dto;\n\n");
        sb.append("import com.google.gson.annotations.SerializedName;\n");
        sb.append("import java.util.List;\n\n");
        sb.append("public class ").append(name).append(" {\n");
        
        if (schema.has("properties")) {
            JsonObject props = schema.getAsJsonObject("properties");
            for (String propName : props.keySet()) {
                JsonObject prop = props.getAsJsonObject(propName);
                String javaType = resolveType(prop);
                sb.append("    @SerializedName(\"").append(propName).append("\")\n");
                sb.append("    private ").append(javaType).append(" ").append(propName).append(";\n\n");
                
                String cName = propName.substring(0,1).toUpperCase() + propName.substring(1);
                sb.append("    public ").append(javaType).append(" get").append(cName).append("() {\n");
                sb.append("        return ").append(propName).append(";\n    }\n\n");
                sb.append("    public void set").append(cName).append("(").append(javaType).append(" ").append(propName).append(") {\n");
                sb.append("        this.").append(propName).append(" = ").append(propName).append(";\n    }\n\n");
            }
        }

        sb.append("    // mock field preserved for ui consistency\n");
        sb.append("}\n");
        return sb.toString();
    }

    private String generateApiInterface(String name, JsonObject paths) {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.app.cinx.api;\n\n");
        sb.append("import com.app.cinx.api.dto.*;\n");
        sb.append("import retrofit2.Call;\n");
        sb.append("import retrofit2.http.*;\n");
        sb.append("import java.util.List;\n\n");
        sb.append("public interface ").append(name).append(" {\n");
        
        for (String pathName : paths.keySet()) {
            JsonObject ops = paths.getAsJsonObject(pathName);
            for (String method : ops.keySet()) {
                JsonObject op = ops.getAsJsonObject(method);
                String methodName = op.has("operationId") ? op.get("operationId").getAsString() : "do" + method.toUpperCase();
                
                String returnType = "Object";
                if(op.has("responses") && op.getAsJsonObject("responses").has("200")) {
                    JsonObject c200 = op.getAsJsonObject("responses").getAsJsonObject("200");
                    if(c200.has("content") && c200.getAsJsonObject("content").has("*/*")) {
                        JsonObject sc = c200.getAsJsonObject("content").getAsJsonObject("*/*").getAsJsonObject("schema");
                        returnType = resolveType(sc);
                    } else if(c200.has("content") && c200.getAsJsonObject("content").has("application/json")) {
                        JsonObject sc = c200.getAsJsonObject("content").getAsJsonObject("application/json").getAsJsonObject("schema");
                        returnType = resolveType(sc);
                    }
                }
                if (returnType.startsWith("ApiResponse")) {
                    String sub = returnType.replace("ApiResponse", "");
                    if(sub.isEmpty() || sub.equals("Object")) sub = "Object";
                    if(sub.equals("TokenResponseDto")) sub = "TokenResponseDto";
                    returnType = "ApiResponse<" + sub + ">";
                }
                if (returnType.startsWith("ApiListResponse")) {
                    String sub = returnType.replace("ApiListResponse", "");
                    returnType = "ApiListResponse<" + sub + ">";
                }

                sb.append("    @").append(method.toUpperCase()).append("(\"").append(pathName.substring(1)).append("\")\n");
                sb.append("    Call<").append(returnType).append("> ").append(methodName).append("(");
                
                boolean hasParam = false;
                if(op.has("parameters")) {
                    JsonArray params = op.getAsJsonArray("parameters");
                    for(int i=0; i<params.size(); i++) {
                        JsonObject p = params.get(i).getAsJsonObject();
                        String pIn = p.get("in").getAsString();
                        String pName = p.get("name").getAsString();
                        String pType = p.has("schema") ? resolveType(p.getAsJsonObject("schema")) : "String";
                        if(pIn.equals("query")) {
                            sb.append("@Query(\"").append(pName).append("\") ").append(pType).append(" ").append(pName);
                            hasParam = true;
                        } else if(pIn.equals("path")) {
                            sb.append("@Path(\"").append(pName).append("\") ").append(pType).append(" ").append(pName);
                            hasParam = true;
                        }
                        if(i < params.size()-1) sb.append(", ");
                    }
                }
                
                if (op.has("requestBody")) {
                    JsonObject rb = op.getAsJsonObject("requestBody");
                    if(rb.has("content") && rb.getAsJsonObject("content").has("application/json")) {
                        JsonObject sc = rb.getAsJsonObject("content").getAsJsonObject("application/json").getAsJsonObject("schema");
                        String bodyType = resolveType(sc);
                        if(hasParam) sb.append(", ");
                        sb.append("@Body ").append(bodyType).append(" body");
                    }
                }

                sb.append(");\n\n");
            }
        }

        sb.append("}\n");
        return sb.toString();
    }

    private String resolveType(JsonObject prop) {
        if(prop.has("$ref")) {
            String ref = prop.get("$ref").getAsString();
            return ref.substring(ref.lastIndexOf('/') + 1);
        }
        if (prop.has("type")) {
            String t = prop.get("type").getAsString();
            if (t.equals("string")) return "String";
            if (t.equals("integer")) {
                if(prop.has("format") && prop.get("format").getAsString().equals("int64")) return "Long";
                return "Integer";
            }
            if (t.equals("number")) return "Double";
            if (t.equals("boolean")) return "Boolean";
            if (t.equals("array") && prop.has("items")) {
                return "List<" + resolveType(prop.getAsJsonObject("items")) + ">";
            }
        }
        return "Object";
    }
}
