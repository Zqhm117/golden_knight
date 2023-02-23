package com.itheima.reggie.entity;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Orders implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String number;

    private Integer status;

    private Long userId;

    private Long addressBookId;//地址ID

    private LocalDateTime orderTime;//下单时间

    private LocalDateTime checkoutTime;//结账时间

    private Integer payMethod;//支付方式

    private BigDecimal amount;//实收金额

    private String remark;//备注

    private String userName;//用户名

    private String phone;//电话

    private String address;//地址

    private String consignee;//收货人
}
