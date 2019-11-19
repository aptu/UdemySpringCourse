package com.udemySpringCourse.app.ws.ui.controller;

import com.udemySpringCourse.app.ws.exceptions.UserServiceException;
import com.udemySpringCourse.app.ws.service.AddressService;
import com.udemySpringCourse.app.ws.service.UserService;
import com.udemySpringCourse.app.ws.shared.dto.AddressDto;
import com.udemySpringCourse.app.ws.ui.model.request.UserDetailsRequestModel;
import com.udemySpringCourse.app.ws.ui.model.response.*;
import com.udemySpringCourse.app.ws.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users") //http://localhost:8080/users
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    AddressService addressService;

    @GetMapping(path="/{id}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable String id){
        UserRest returnValue = new UserRest();
        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnValue);
        return returnValue;
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
        UserRest returnValue = new UserRest();

        if (userDetails.getFirstName().isEmpty()) {
            throw new NullPointerException("Object is NULL LOL");
        }
//        UserDto userDto = new UserDto();
//        BeanUtils.copyProperties(userDetails, userDto); Shallow

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
//        BeanUtils.copyProperties(createdUser, returnValue);
        returnValue = modelMapper.map(createdUser, UserRest.class);

        return returnValue;
    }

    @PutMapping(path="/{id}",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest updateUser(@RequestBody UserDetailsRequestModel userDetails, @PathVariable String id) {

        UserRest returnValue = new UserRest();
//        if (userDetails.getFirstName().isEmpty()) {
//            throw new NullPointerException("Object is NULL LOL");
//        }
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updatedUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @DeleteMapping(path="/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);
        returnValue.setOprerationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }

    //parameters passed as a query srting - use @RequestParam
    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public List<UserRest> getUsers(@RequestParam(value="page", defaultValue ="0") int page,
                                   @RequestParam(value="limit", defaultValue="25")int limit) {
        List<UserRest> returnValue = new ArrayList<>();

        List <UserDto> users = userService.getUsers(page, limit);

        //convert type
        for (UserDto userDto: users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }
        return returnValue;
    }

    //http://localhost:8080/web-app/users/userid/addresses/
    @GetMapping(path="/{id}/addresses", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public List<AddressesRest> getUserAddresses(@PathVariable String id){
        List<AddressesRest> returnValue = new ArrayList<>();

        List<AddressDto> addressesDto = addressService.getAddresses(id);

        if (addressesDto != null && ! addressesDto.isEmpty()) {
            ModelMapper modelMapper = new ModelMapper();
            java.lang.reflect.Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
            returnValue = modelMapper.map(addressesDto, listType);
        }

        return returnValue;
    }

    //http://localhost:8080/web-app/users/userid/addresses/addressId
    @GetMapping(path="/{userId}/addresses/{addressId}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public AddressesRest getUserAddressesByAddressId(@PathVariable String addressId){
        AddressDto addressesDto = addressService.getAddress(addressId);
        ModelMapper modelMapper = new ModelMapper();

        return modelMapper.map(addressesDto, AddressesRest.class);

    }
}