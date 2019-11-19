package com.udemySpringCourse.app.ws.service.implementation;

import com.udemySpringCourse.app.ws.io.entity.AddressEntity;
import com.udemySpringCourse.app.ws.io.entity.UserEntity;
import com.udemySpringCourse.app.ws.io.repositories.AddressRepository;
import com.udemySpringCourse.app.ws.io.repositories.UserRepository;
import com.udemySpringCourse.app.ws.service.AddressService;
import com.udemySpringCourse.app.ws.shared.dto.AddressDto;
import org.apache.tomcat.util.digester.ArrayStack;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImplementation  implements AddressService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public List<AddressDto> getAddresses(String userId) {
        List<AddressDto> returnValue = new ArrayStack<>();
        ModelMapper modelMapper = new ModelMapper();
        //query for user id
        UserEntity userEntity = userRepository.findByUserId(userId);

        userEntity.getAddresses();
        if (userEntity == null) return  returnValue;
        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);

        for (AddressEntity addressEntity: addresses) {
            returnValue.add(modelMapper.map(addressEntity, AddressDto.class));
        }
        return returnValue;
    }

    @Override
    public AddressDto getAddress(String addressId) {
        AddressDto returnValue = null;
        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
        if (addressEntity != null) {
            returnValue = new ModelMapper().map(addressEntity, AddressDto.class);
        }
        return returnValue;
    }

}
