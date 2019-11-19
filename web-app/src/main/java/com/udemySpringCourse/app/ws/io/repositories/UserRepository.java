package com.udemySpringCourse.app.ws.io.repositories;

import com.udemySpringCourse.app.ws.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
    UserEntity findByEmail(String email); //queries database
    UserEntity findByUserId(String userId);
    //UserEntity findByLastName(String lastName);
}
