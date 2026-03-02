package com.example.machinesshop.entity;

import com.example.machinesshop.ENUM.TYPE_EMAIL;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessage {
    private String to;
    private String subject;
    private String body;
    private TYPE_EMAIL type;
}
