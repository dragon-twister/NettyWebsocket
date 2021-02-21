package com.coocaa.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * @author bijiahao
 * @date : 2019/6/14.
 * @description Mybatis-plus查询操作模板
 *
 */
public class QueryTemplate<T> extends QueryWrapper {

    public static <T> QueryTemplate<T> getEqQueryTemplate(String column, Object param){
        QueryTemplate<T> queryTemplate = new QueryTemplate();
        queryTemplate.eq(column, param);
        return queryTemplate;
    }

    public static  QueryTemplate getQueryTemplate(){
        QueryTemplate queryTemplate = new QueryTemplate();
        return queryTemplate;
    }
}
