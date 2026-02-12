package com.example.machinesshop.service;

import com.example.machinesshop.ENUM.TYPE_EMAIL;
import com.example.machinesshop.entity.EmailMessage;
import com.example.machinesshop.entity.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailQueueService {
    private AmqpTemplate rabbitTemplate;

    public void senWelcomeEmail(User user) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setTo(user.getEmail());
        emailMessage.setSubject("🚢 Chào mừng đến với cửa hàng Hằng Hải");

        // Dùng String.format để đưa username vào đúng vị trí %s
        String body = String.format(
                "Xin chào %s,\n\n" +
                        "Cảm ơn bạn đã tin tưởng và đăng ký thành viên tại Cửa hàng Hàng Hải. " +
                        "Chúng tôi rất hân hạnh được đồng hành cùng bạn trên mọi hành trình.\n\n" +
                        "Chúc bạn có những trải nghiệm mua sắm tuyệt vời!"+
                        "Vũ Văn Quân - 0867996087"+
                        "Canh Nậu, Tây Phương - Hà Nội"
                ,
                user.getUsername()
        );

        emailMessage.setBody(body);
        emailMessage.setType(TYPE_EMAIL.WELCOME); // Convert Enum sang String để .NET dễ đọc

        // QUAN TRỌNG: Tên queue phải khớp với bên .NET (email_queue)
        rabbitTemplate.convertAndSend("email_queue", emailMessage);

    }
}
