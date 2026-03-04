package com.example.wasla.mapper;


import com.example.wasla.dto.request.ClientRegisterRequest;
import com.example.wasla.dto.request.DriverRegisterRequest;
import com.example.wasla.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {


    User toEntity(ClientRegisterRequest request);
    User toEntity(DriverRegisterRequest request);


}
