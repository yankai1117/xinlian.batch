package com.example.batch.dao;


import com.example.batch.entity.VehicleInfoBean;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface VehicleInfoDAO {

	VehicleInfoBean findByPlateNumAndName(VehicleInfoBean vo);

	int insert(VehicleInfoBean vo);
}
