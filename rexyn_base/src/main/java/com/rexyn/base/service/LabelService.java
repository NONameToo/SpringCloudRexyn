package com.rexyn.base.service;

import com.rexyn.base.dao.LabelDao;
import com.rexyn.base.pojo.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.IdGenerator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName fushaokai
 * @Description TODO
 * @Author Administrator
 * @Date 2019/8/9 0009 16:48
 * @Version 1.0
 **/

@Service
@Transactional
public class LabelService {

    @Autowired
    private LabelDao labelDao;
    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private RedisTemplate redisTemplate;




    public List<Label> findAll() {
        return labelDao.findAll();
    }

    public Label findById(String id) {
        // 先从缓存中查询
        Label label = (Label)redisTemplate.opsForValue().get("label" + id);
        // 如果没有取到，从数据库中查询
        if(label == null){
            label = labelDao.findById(id).get();
            // 把查询到的数据放到redis缓存中
            redisTemplate.opsForValue().set("label" + id,label,60, TimeUnit.MINUTES);
        }
        return label;
    }


    public void save(Label label) {
        label.setId(idGenerator.nextId() + "");
        labelDao.save(label);
    }

    public void update(Label label) {
        // 修改或删除要删除redis中的缓存
        redisTemplate.delete("label" + label.getId());
        labelDao.save(label);
    }

    public void deleteById(String id) {
        // 修改或删除要删除redis中的缓存
        redisTemplate.delete("label" + id);
        labelDao.deleteById(id);
    }


    public List<Label> findSearch(Label label) {
        return labelDao.findAll(new Specification<Label>() {
            /**
             *
             * @param root                  根对象，也就是要把条件封装到那个对象中
             * @param criteriaQuery         封装查询的关键字  比如order by  order by
             * @param criteriaBuilder       用来封装条件对象，如果直接返回null 则表示不需要任何条件
             * @return
             */
            @Override
            public Predicate toPredicate(Root<Label> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                // 创建一个List保存查询的条件
                ArrayList<Predicate> predicateList = new ArrayList<>();
                // 组装对象的查询条件
                if (label.getLabelname() != null && !"".equals(label.getLabelname())) {
                    Predicate predicate = criteriaBuilder.like(root.get("labelname").as(String.class), "%" + label.getLabelname() + "%");  // where label name like %张三%
                    predicateList.add(predicate);
                }
                if (label.getState() != null & !"".equals(label.getState())) {
                    Predicate predicate = criteriaBuilder.equal(root.get("state").as(String.class), label.getState());
                    predicateList.add(predicate);
                }

                // 最终需要的是一个数组
                Predicate[] parr = new Predicate[predicateList.size()];
                parr = predicateList.toArray(parr);
                return criteriaBuilder.and(parr);           // 返回的是一个封装的条件数组  where labelname like %张三% and state = 1
            }
        });

    }


    public Page<Label> pageQuery(Label label, Integer page, Integer size) {

        // 封装分页对象，这个框架页码是从0开始的，所以我们给他-1
        Pageable pageable = PageRequest.of(page - 1, size);

        return labelDao.findAll(new Specification<Label>() {
            @Override
            public Predicate toPredicate(Root<Label> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                // 创建一个List保存查询的条件
                ArrayList<Predicate> predicateList = new ArrayList<>();
                // 组装对象的查询条件
                if (label.getLabelname() != null && !"".equals(label.getLabelname())) {
                    Predicate predicate = criteriaBuilder.like(root.get("labelname").as(String.class), "%" + label.getLabelname() + "%");  // where label name like %张三%
                    predicateList.add(predicate);
                }
                if (label.getState() != null & !"".equals(label.getState())) {
                    Predicate predicate = criteriaBuilder.equal(root.get("state").as(String.class), label.getState());
                    predicateList.add(predicate);
                }

                // 最终需要的是一个数组
                Predicate[] parr = new Predicate[predicateList.size()];
                parr = predicateList.toArray(parr);
                return criteriaBuilder.and(parr);           // 返回的是一个封装的条件数组  where labelname like %张三% and state = 1
            }
        }, pageable);
    }
}
