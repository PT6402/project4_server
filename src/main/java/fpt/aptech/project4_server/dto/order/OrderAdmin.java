/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fpt.aptech.project4_server.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author macos
 */
public class OrderAdmin {

    private int paymentStatus;

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    private String UserName;

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private int orderId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creatDate;
    private List<OrderDetailDto> orderDetails;

    // Constructor
    public OrderAdmin(int orderId, LocalDateTime creatDate, List<OrderDetailDto> orderDetails,
            int paymentStatus,String UserName,String email) {
        this.orderId = orderId;
        this.creatDate = creatDate;
        this.orderDetails = orderDetails;
        this.paymentStatus = paymentStatus;
        this.UserName=UserName;
        this.email=email;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public List<OrderDetailDto> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailDto> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public LocalDateTime getCreatDate() {
        return creatDate;
    }

    public void setCreatDate(LocalDateTime creatDate) {
        this.creatDate = creatDate;
    }
}
