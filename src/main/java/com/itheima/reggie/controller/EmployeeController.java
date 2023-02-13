package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee){

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        /*LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);*/
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();  // 查询对象
        queryWrapper.eq(Employee::getUsername, employee.getUsername()); // 等值查询
        Employee emp = employeeService.getOne(queryWrapper);  // 唯一的数据

        //3、如果没有查询到则返回登录失败结果
        /*if(emp == null){
            return R.error("登录失败");
        }*/
        if(emp == null){
            return R.error("登录失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        /*if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }*/
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
//        if(emp.getStatus() == 0){
//            return R.error("账号已禁用");
//        }
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
//        request.getSession().setAttribute("employee",emp.getId());
//        return R.success(emp);
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee"); // 清理保存的id
        return R.success("退出成功");
    }
    @PostMapping()
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        //1、服务端Controller接收页面提交的数据并调用Service将数据进行保存
        log.info("新增员工， 员工信息：{}", employee.toString());
        //设置初始密码123456需要MD5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //employee.setCreateTime(LocalDateTime.now());  // 记录创建时间
        //employee.setUpdateTime(LocalDateTime.now());  // 记录更新时间
        //Long empID = (Long) request.getSession().getAttribute("employee");  //获得当前登录用户id，强转
        //employee.setCreateUser(empID);  //设置是谁创建的用户
        //employee.setUpdateUser(empID);  //设置是谁更新的用户

        //2、Service调用Mapper操作数据库，保存数据
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);
        // 1、构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        // 2、构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        // 过滤条件1：名字
        if(name != null){
            queryWrapper.like(Employee::getName, name);
        }
        // 排序条件：更新时间
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        // 3、执行查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据ID修改员工信息
     */
    @PutMapping
    public R<String> updata(HttpServletRequest request, @RequestBody Employee employee){
        log.info(employee.toString());
        employee.setUpdateTime(LocalDateTime.now());  // 记录修改员工的时间
        Long empId = (Long)request.getSession().getAttribute("employee");  // 获取当前用户
        employee.setUpdateUser(empId);  // 记录修改员工的用户
        employeeService.updateById(employee);
        return null;
    }

    /**
     * 根据ID查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据ID查询员工信息：...");
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到员工");
    }
}
