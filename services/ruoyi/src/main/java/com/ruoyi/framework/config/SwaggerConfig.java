package com.ruoyi.framework.config;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ruoyi.common.utils.StringUtils;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * OpenAPI 接口文档配置
 *
 * @author ruoyi
 */
@Configuration
public class SwaggerConfig
{
    /** 系统基础配置 */
    @Autowired
    private RuoYiConfig ruoyiConfig;

    /** 设置请求的统一前缀 */
    @Value("${swagger.pathMapping:}")
    private String pathMapping;

    @Bean
    public OpenAPI openApi()
    {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("标题：若依管理系统接口文档")
                        .description("描述：用于管理集团旗下公司的人员信息,具体包括XXX,XXX模块...")
                        .contact(new Contact().name(ruoyiConfig.getName()))
                        .version("版本号：" + ruoyiConfig.getVersion()))
                .components(new Components().addSecuritySchemes("Authorization",
                        new SecurityScheme().type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"));

        if (StringUtils.isNotEmpty(pathMapping))
        {
            openAPI.setServers(Collections.singletonList(new Server().url(pathMapping)));
        }
        return openAPI;
    }
}
