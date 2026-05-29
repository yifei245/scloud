package com.scloud.generator.controller;

import cn.hutool.core.util.StrUtil;
import com.scloud.common.core.Result;
import com.scloud.common.security.RequirePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/generator")
public class GeneratorController {
    private final DataSource dataSource;

    @GetMapping("/tables")
    @RequirePermission("generator:query")
    public Result<List<String>> tables() throws Exception {
        List<String> tables = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet rs = metaData.getTables(connection.getCatalog(), null, "%", new String[]{"TABLE"})) {
                while (rs.next()) tables.add(rs.getString("TABLE_NAME"));
            }
        }
        return Result.ok(tables);
    }

    @GetMapping("/tables/{tableName}/columns")
    @RequirePermission("generator:query")
    public Result<List<Map<String, String>>> columns(@PathVariable String tableName) throws Exception {
        List<Map<String, String>> columns = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet rs = metaData.getColumns(connection.getCatalog(), null, tableName, "%")) {
                while (rs.next()) {
                    Map<String, String> column = new LinkedHashMap<>();
                    column.put("columnName", rs.getString("COLUMN_NAME"));
                    column.put("jdbcType", rs.getString("TYPE_NAME"));
                    column.put("comment", rs.getString("REMARKS"));
                    columns.add(column);
                }
            }
        }
        return Result.ok(columns);
    }

    @PostMapping("/code")
    @RequirePermission("generator:code")
    public Result<Map<String, String>> code(@RequestBody Map<String, Object> body) throws Exception {
        String tableName = String.valueOf(body.get("tableName"));
        String className = toClassName(tableName);
        String variableName = StrUtil.lowerFirst(className);
        String pkg = String.valueOf(body.getOrDefault("packageName", "com.scloud.generated"));
        String permissionPrefix = String.valueOf(body.getOrDefault("permissionPrefix",
                StrUtil.toUnderlineCase(className).replace("_", ":")));
        List<ColumnMeta> columns = loadColumns(tableName);
        String idType = findIdType(columns);
        Map<String, String> files = new LinkedHashMap<>();
        files.put(className + "DO.java", entityCode(pkg, className, tableName, columns));
        files.put(className + "Mapper.java", mapperCode(pkg, className));
        files.put(className + "Service.java", serviceCode(pkg, className, variableName, idType, columns));
        files.put(className + "Controller.java", controllerCode(pkg, className, variableName, idType, permissionPrefix));
        files.put(className + "DTO.java", dtoCode(pkg, className, columns));
        files.put(className + "SaveRequest.java", saveRequestCode(pkg, className, columns));
        files.put(className + "PageRequest.java", pageRequestCode(pkg, className, columns));
        files.put(className + "VO.java", voCode(pkg, className, columns));
        applyCustomTemplates(files, body, pkg, tableName, className, columns);
        return Result.ok(files);
    }

    @SuppressWarnings("unchecked")
    private void applyCustomTemplates(Map<String, String> files, Map<String, Object> body, String pkg,
            String tableName, String className, List<ColumnMeta> columns) {
        Object templates = body.get("templates");
        if (!(templates instanceof Map<?, ?> templateMap)) {
            return;
        }
        String fields = columns.stream()
                .map(column -> "private " + column.javaType + " " + column.fieldName + ";")
                .collect(Collectors.joining("\n"));
        for (Map.Entry<?, ?> entry : templateMap.entrySet()) {
            String fileName = String.valueOf(entry.getKey())
                    .replace("{{className}}", className)
                    .replace("{{packageName}}", pkg)
                    .replace("{{tableName}}", tableName);
            String content = String.valueOf(entry.getValue())
                    .replace("{{className}}", className)
                    .replace("{{packageName}}", pkg)
                    .replace("{{tableName}}", tableName)
                    .replace("{{fields}}", fields);
            files.put(fileName, content);
        }
    }

    private List<ColumnMeta> loadColumns(String tableName) throws Exception {
        Set<String> primaryKeys;
        List<ColumnMeta> columns = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet rs = metaData.getPrimaryKeys(connection.getCatalog(), null, tableName)) {
                List<String> keys = new ArrayList<>();
                while (rs.next()) {
                    keys.add(rs.getString("COLUMN_NAME"));
                }
                primaryKeys = keys.stream().collect(Collectors.toSet());
            }
            try (ResultSet rs = metaData.getColumns(connection.getCatalog(), null, tableName, "%")) {
                while (rs.next()) {
                    ColumnMeta column = new ColumnMeta();
                    column.columnName = rs.getString("COLUMN_NAME");
                    column.fieldName = toFieldName(column.columnName);
                    column.jdbcType = rs.getString("TYPE_NAME");
                    column.javaType = toJavaType(column.jdbcType);
                    column.comment = StrUtil.blankToDefault(rs.getString("REMARKS"), column.columnName);
                    column.primaryKey = primaryKeys.contains(column.columnName);
                    columns.add(column);
                }
            }
        }
        return columns;
    }

    private String entityCode(String pkg, String className, String tableName, List<ColumnMeta> columns) {
        StringBuilder code = new StringBuilder();
        code.append("package ").append(pkg).append(".entity;\n\n")
                .append("import com.baomidou.mybatisplus.annotation.IdType;\n")
                .append("import com.baomidou.mybatisplus.annotation.TableId;\n")
                .append("import com.baomidou.mybatisplus.annotation.TableName;\n")
                .append("import java.math.BigDecimal;\n")
                .append("import java.time.LocalDateTime;\n")
                .append("import lombok.Data;\n\n")
                .append("@Data\n")
                .append("@TableName(\"").append(tableName).append("\")\n")
                .append("public class ").append(className).append("DO {\n");
        for (ColumnMeta column : columns) {
            if (column.primaryKey) {
                code.append("    @TableId(type = IdType.AUTO)\n");
            }
            code.append("    private ").append(column.javaType).append(" ").append(column.fieldName).append(";\n");
        }
        return code.append("}\n").toString();
    }

    private String mapperCode(String pkg, String className) {
        return "package " + pkg + ".mapper;\n\n"
                + "import com.baomidou.mybatisplus.core.mapper.BaseMapper;\n"
                + "import " + pkg + ".entity." + className + "DO;\n\n"
                + "public interface " + className + "Mapper extends BaseMapper<" + className + "DO> {\n"
                + "}\n";
    }

    private String serviceCode(String pkg, String className, String variableName, String idType, List<ColumnMeta> columns) {
        StringBuilder query = new StringBuilder();
        for (ColumnMeta column : columns) {
            if (column.primaryKey) {
                continue;
            }
            if ("String".equals(column.javaType)) {
                query.append("                .like(StrUtil.isNotBlank(request.get").append(upperFirst(column.fieldName))
                        .append("()), ").append(className).append("DO::get").append(upperFirst(column.fieldName))
                        .append(", request.get").append(upperFirst(column.fieldName)).append("())\n");
            } else {
                query.append("                .eq(request.get").append(upperFirst(column.fieldName))
                        .append("() != null, ").append(className).append("DO::get").append(upperFirst(column.fieldName))
                        .append(", request.get").append(upperFirst(column.fieldName)).append("())\n");
            }
        }
        return "package " + pkg + ".service;\n\n"
                + "import cn.hutool.core.util.StrUtil;\n"
                + "import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;\n"
                + "import com.baomidou.mybatisplus.extension.plugins.pagination.Page;\n"
                + "import " + pkg + ".entity." + className + "DO;\n"
                + "import " + pkg + ".mapper." + className + "Mapper;\n"
                + "import " + pkg + ".vo." + className + "PageRequest;\n"
                + "import " + pkg + ".vo." + className + "SaveRequest;\n"
                + "import lombok.RequiredArgsConstructor;\n"
                + "import org.springframework.beans.BeanUtils;\n"
                + "import org.springframework.stereotype.Service;\n\n"
                + "@Service\n"
                + "@RequiredArgsConstructor\n"
                + "public class " + className + "Service {\n"
                + "    private final " + className + "Mapper " + variableName + "Mapper;\n\n"
                + "    public Page<" + className + "DO> page(" + className + "PageRequest request) {\n"
                + "        LambdaQueryWrapper<" + className + "DO> query = new LambdaQueryWrapper<" + className + "DO>()\n"
                + query
                + "                .orderByDesc(" + className + "DO::get" + upperFirst(findIdField(columns)) + ");\n"
                + "        return " + variableName + "Mapper.selectPage(new Page<>(request.getPageNo(), request.getPageSize()), query);\n"
                + "    }\n\n"
                + "    public " + className + "DO get(" + idType + " id) {\n"
                + "        return " + variableName + "Mapper.selectById(id);\n"
                + "    }\n\n"
                + "    public " + className + "DO create(" + className + "SaveRequest request) {\n"
                + "        " + className + "DO entity = new " + className + "DO();\n"
                + "        BeanUtils.copyProperties(request, entity);\n"
                + "        " + variableName + "Mapper.insert(entity);\n"
                + "        return entity;\n"
                + "    }\n\n"
                + "    public " + className + "DO update(" + idType + " id, " + className + "SaveRequest request) {\n"
                + "        " + className + "DO entity = new " + className + "DO();\n"
                + "        BeanUtils.copyProperties(request, entity);\n"
                + "        entity.set" + upperFirst(findIdField(columns)) + "(id);\n"
                + "        " + variableName + "Mapper.updateById(entity);\n"
                + "        return get(id);\n"
                + "    }\n\n"
                + "    public void delete(" + idType + " id) {\n"
                + "        " + variableName + "Mapper.deleteById(id);\n"
                + "    }\n"
                + "}\n";
    }

    private String controllerCode(String pkg, String className, String variableName, String idType,
            String permissionPrefix) {
        String basePath = "/" + StrUtil.toUnderlineCase(className).replace("_", "-");
        return "package " + pkg + ".controller;\n\n"
                + "import com.scloud.common.core.Result;\n"
                + "import com.scloud.common.security.RequirePermission;\n"
                + "import " + pkg + ".service." + className + "Service;\n"
                + "import " + pkg + ".vo." + className + "PageRequest;\n"
                + "import " + pkg + ".vo." + className + "SaveRequest;\n"
                + "import io.swagger.v3.oas.annotations.Operation;\n"
                + "import io.swagger.v3.oas.annotations.tags.Tag;\n"
                + "import jakarta.validation.Valid;\n"
                + "import lombok.RequiredArgsConstructor;\n"
                + "import org.springframework.web.bind.annotation.DeleteMapping;\n"
                + "import org.springframework.web.bind.annotation.GetMapping;\n"
                + "import org.springframework.web.bind.annotation.PathVariable;\n"
                + "import org.springframework.web.bind.annotation.PostMapping;\n"
                + "import org.springframework.web.bind.annotation.PutMapping;\n"
                + "import org.springframework.web.bind.annotation.RequestBody;\n"
                + "import org.springframework.web.bind.annotation.RequestMapping;\n"
                + "import org.springframework.web.bind.annotation.RestController;\n\n"
                + "@Tag(name = \"" + className + "\")\n"
                + "@RestController\n"
                + "@RequiredArgsConstructor\n"
                + "@RequestMapping(\"" + basePath + "\")\n"
                + "public class " + className + "Controller {\n"
                + "    private final " + className + "Service " + variableName + "Service;\n\n"
                + "    @Operation(summary = \"分页查询\")\n"
                + "    @RequirePermission(\"" + permissionPrefix + ":query\")\n"
                + "    @GetMapping\n"
                + "    public Result<?> page(" + className + "PageRequest request) {\n"
                + "        return Result.ok(" + variableName + "Service.page(request));\n"
                + "    }\n\n"
                + "    @Operation(summary = \"详情\")\n"
                + "    @RequirePermission(\"" + permissionPrefix + ":query\")\n"
                + "    @GetMapping(\"/{id}\")\n"
                + "    public Result<?> get(@PathVariable " + idType + " id) {\n"
                + "        return Result.ok(" + variableName + "Service.get(id));\n"
                + "    }\n\n"
                + "    @Operation(summary = \"新增\")\n"
                + "    @RequirePermission(\"" + permissionPrefix + ":create\")\n"
                + "    @PostMapping\n"
                + "    public Result<?> create(@Valid @RequestBody " + className + "SaveRequest request) {\n"
                + "        return Result.ok(" + variableName + "Service.create(request));\n"
                + "    }\n\n"
                + "    @Operation(summary = \"修改\")\n"
                + "    @RequirePermission(\"" + permissionPrefix + ":update\")\n"
                + "    @PutMapping(\"/{id}\")\n"
                + "    public Result<?> update(@PathVariable " + idType + " id, @Valid @RequestBody " + className + "SaveRequest request) {\n"
                + "        return Result.ok(" + variableName + "Service.update(id, request));\n"
                + "    }\n\n"
                + "    @Operation(summary = \"删除\")\n"
                + "    @RequirePermission(\"" + permissionPrefix + ":delete\")\n"
                + "    @DeleteMapping(\"/{id}\")\n"
                + "    public Result<?> delete(@PathVariable " + idType + " id) {\n"
                + "        " + variableName + "Service.delete(id);\n"
                + "        return Result.ok(true);\n"
                + "    }\n"
                + "}\n";
    }

    private String saveRequestCode(String pkg, String className, List<ColumnMeta> columns) {
        StringBuilder fields = new StringBuilder();
        for (ColumnMeta column : columns) {
            if (column.primaryKey || isAuditColumn(column.fieldName)) {
                continue;
            }
            fields.append(schema(column)).append("    private ").append(column.javaType).append(" ")
                    .append(column.fieldName).append(";\n\n");
        }
        return requestClassCode(pkg, className + "SaveRequest", className + "保存请求", fields.toString());
    }

    private String dtoCode(String pkg, String className, List<ColumnMeta> columns) {
        StringBuilder fields = new StringBuilder();
        for (ColumnMeta column : columns) {
            if (column.primaryKey || isAuditColumn(column.fieldName)) {
                continue;
            }
            fields.append(schema(column)).append("    private ").append(column.javaType).append(" ")
                    .append(column.fieldName).append(";\n\n");
        }
        return requestClassCode(pkg, className + "DTO", className + "传输对象", fields.toString());
    }

    private String pageRequestCode(String pkg, String className, List<ColumnMeta> columns) {
        StringBuilder fields = new StringBuilder();
        fields.append("    @Schema(description = \"页码，从 1 开始\", example = \"1\")\n")
                .append("    private Long pageNo = 1L;\n\n")
                .append("    @Schema(description = \"每页条数，最大 100\", example = \"10\")\n")
                .append("    private Long pageSize = 10L;\n\n");
        for (ColumnMeta column : columns) {
            if (column.primaryKey || isAuditColumn(column.fieldName)) {
                continue;
            }
            fields.append(schema(column)).append("    private ").append(column.javaType).append(" ")
                    .append(column.fieldName).append(";\n\n");
        }
        fields.append("    public Long getPageNo() {\n")
                .append("        return pageNo == null || pageNo < 1 ? 1L : pageNo;\n")
                .append("    }\n\n")
                .append("    public Long getPageSize() {\n")
                .append("        if (pageSize == null || pageSize < 1) {\n")
                .append("            return 10L;\n")
                .append("        }\n")
                .append("        return Math.min(pageSize, 100L);\n")
                .append("    }\n");
        return requestClassCode(pkg, className + "PageRequest", className + "分页查询", fields.toString());
    }

    private String voCode(String pkg, String className, List<ColumnMeta> columns) {
        StringBuilder fields = new StringBuilder();
        for (ColumnMeta column : columns) {
            fields.append(schema(column)).append("    private ").append(column.javaType).append(" ")
                    .append(column.fieldName).append(";\n\n");
        }
        return requestClassCode(pkg, className + "VO", className + "响应", fields.toString());
    }

    private String requestClassCode(String pkg, String className, String description, String fields) {
        return "package " + pkg + ".vo;\n\n"
                + "import io.swagger.v3.oas.annotations.media.Schema;\n"
                + "import java.math.BigDecimal;\n"
                + "import java.time.LocalDateTime;\n"
                + "import lombok.Data;\n\n"
                + "@Data\n"
                + "@Schema(description = \"" + description + "\")\n"
                + "public class " + className + " {\n"
                + fields
                + "}\n";
    }

    private String schema(ColumnMeta column) {
        return "    @Schema(description = \"" + escape(column.comment) + "\")\n";
    }

    private String findIdField(List<ColumnMeta> columns) {
        return columns.stream().filter(column -> column.primaryKey).findFirst()
                .map(column -> column.fieldName).orElse("id");
    }

    private String findIdType(List<ColumnMeta> columns) {
        return columns.stream().filter(column -> column.primaryKey).findFirst()
                .map(column -> column.javaType).orElse("Long");
    }

    private String toClassName(String tableName) {
        StringBuilder builder = new StringBuilder();
        for (String part : tableName.split("_")) {
            builder.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
        }
        return builder.toString();
    }

    private String toFieldName(String columnName) {
        String className = toClassName(columnName);
        return StrUtil.lowerFirst(className);
    }

    private String upperFirst(String value) {
        return StrUtil.upperFirst(value);
    }

    private String toJavaType(String jdbcType) {
        String type = jdbcType == null ? "" : jdbcType.toUpperCase();
        if (type.contains("BIGINT")) {
            return "Long";
        }
        if (type.contains("INT") || type.contains("TINYINT") || type.contains("SMALLINT")) {
            return "Integer";
        }
        if (type.contains("DECIMAL") || type.contains("NUMERIC")) {
            return "BigDecimal";
        }
        if (type.contains("DATE") || type.contains("TIME")) {
            return "LocalDateTime";
        }
        return "String";
    }

    private boolean isAuditColumn(String fieldName) {
        return "createTime".equals(fieldName) || "updateTime".equals(fieldName);
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static class ColumnMeta {
        private String columnName;
        private String fieldName;
        private String jdbcType;
        private String javaType;
        private String comment;
        private boolean primaryKey;
    }
}
