package lk.ijse.eebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class OrderDTO {
    private int orderId;
    private String customerId;
    private double total;
    private Date date;

}
