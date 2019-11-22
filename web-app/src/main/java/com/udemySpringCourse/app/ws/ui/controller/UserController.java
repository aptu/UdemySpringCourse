package com.udemySpringCourse.app.ws.ui.controller;

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
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/users") //http://localhost:8080/users
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
    @GetMapping(path="/{id}/addresses", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE,
            "application/hal+json", "application/hal+xml"})
    public Resources<AddressesRest> getUserAddresses(@PathVariable String id){
        List<AddressesRest> addressesListRestModel = new ArrayList<>();

        List<AddressDto> addressesDto = addressService.getAddresses(id);

        if (addressesDto != null && ! addressesDto.isEmpty()) {
            ModelMapper modelMapper = new ModelMapper();
            java.lang.reflect.Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
            addressesListRestModel = modelMapper.map(addressesDto, listType);

            //create links for each element
            for (AddressesRest addressRest: addressesListRestModel) {
                Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId())).withRel("self");
                addressRest.add(addressLink);

                Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
                addressRest.add(userLink);

            }
        }

        return new Resources<>(addressesListRestModel);
    }

    //http://localhost:8080/web-app/users/userid/addresses/addressId
    @GetMapping(path="/{userId}/addresses/{addressId}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE,
                "application/hal+json", "application/hal+xml"})
    public Resource<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId){
        AddressDto addressesDto = addressService.getAddress(addressId);
        ModelMapper modelMapper = new ModelMapper();

        Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
        Link userLink = linkTo(UserController.class).slash(userId).withRel("user");

        //old w/o methodOn
        //Link addressesLink = linkTo(UserController.class).slash(userId).slash("addresses").withRel("addresses");
        //with methodOn
        Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");

        AddressesRest addressesRestModel = modelMapper.map(addressesDto, AddressesRest.class);
        addressesRestModel.add(addressLink); //add a link
        addressesRestModel.add(userLink); //add a link
        addressesRestModel.add(addressesLink); //add a link

        return new Resource<>(addressesRestModel);
    }

    //http://localhost:8080/web-app/users/email-verification?token=dsakj
    @GetMapping(path="/email-verification", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);
        if (isVerified) {
            returnValue.setOprerationResult(RequestOperationStatus.SUCCESS.name());
        } else {
            returnValue.setOprerationResult(RequestOperationStatus.ERROR.name());
        }
        return returnValue;
    }

}
