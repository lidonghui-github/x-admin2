package com.lantu;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * @author lidonghui
 * @date 2023年03月16日 6:51
 */
public class CodeGenerator {
    public static void main(String[] args) {
        final String url="jdbc:mysql:///xdb";
        final String username="root";
        final String password="123456";
        final String outputDir="D:\\workspace\\x-admin2\\src\\main\\java";
        final String moduleName="sys";
        final String mapperLocation="D:\\workspace\\x-admin2\\src\\main\\resources\\mapper\\"+moduleName;
        final String tables="x_user,x_role,x_menu,x_user_role,x_role_menu";
        final String tablePrefix="x_";

        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("lidonghui") // 设置作者
                            // .enableSwagger() // 开启 swagger 模式
                            ///   .fileOverride() // 覆盖已生成文件
                            .outputDir(outputDir); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.lantu") // 设置父包名
                            .moduleName(moduleName) // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, mapperLocation)); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tables) // 设置需要生成的表名
                            .addTablePrefix(tablePrefix); // 设置过滤表前缀
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
