package com.library.bible.cart.Mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

import com.library.bible.cart.model.Cart;

@Mapper  
public interface CartMapper {

	 @Select({
		 "<script>",
		 "SELECT * FROM cart WHERE cart_id IN ",
	     "<foreach item='id' collection='CartId' open='(' separator=',' close=')'>",
		 "#{id}",
	     "</foreach>",
	     "</script>"
	     })
	    List<Cart> getSelectedCarts(@Param("cartIds") List<Long> cartIds); 
	}