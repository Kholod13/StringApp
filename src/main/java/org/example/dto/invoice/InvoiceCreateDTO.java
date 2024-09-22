package org.example.dto.invoice;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class InvoiceCreateDTO {
    private String name;
    private String location;
    private MultipartFile file;
    private Double amount;
}