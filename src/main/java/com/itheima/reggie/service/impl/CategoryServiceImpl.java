package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分类，删除前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //查询当前分类是否关联菜品和套餐
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int dish_count = dishService.count(dishLambdaQueryWrapper);
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int setmeal_count = setmealService.count(setmealLambdaQueryWrapper);
        if(dish_count>0){
            throw new CustomException("当前分类关联了菜品，不能删除！");
        }
        if(setmeal_count>0){
            throw new CustomException("当前分类关联了套餐，不能删除！");
        }
        super.removeById(id);
    }
}
