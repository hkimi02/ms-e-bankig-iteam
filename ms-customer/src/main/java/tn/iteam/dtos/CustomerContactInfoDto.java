package tn.iteam.dtos;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "customers")
public record CustomerContactInfoDto(String message,
                                     Map<String, String> contactDetails,
                                     List<String> onCallSupport) {
}
