package com.merxport.trading.config;

import com.merxport.trading.util.ImageUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageConfig
{
    @Bean
    public ImageUtil resizeImage()
    {
        return new ImageUtil();
    }
}
