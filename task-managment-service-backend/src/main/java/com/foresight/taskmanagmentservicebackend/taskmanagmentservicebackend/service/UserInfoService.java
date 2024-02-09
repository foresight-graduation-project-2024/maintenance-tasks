package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.service;

import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection.UserInfo;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.exception.ErrorCode;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.exception.RuntimeErrorCodedException;
import com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.repo.UserInfoRepo;
import com.mongodb.MongoWriteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoService {
    private final UserInfoRepo userInfoRepo;

    public void addUser(UserInfo userInfo)
    {
        try
        {
            userInfoRepo.save(userInfo);
        }
        catch (Exception ignored)
        {

        }

    }

    public UserInfo getUserByEmail(String email)
    {
        return userInfoRepo
                .findByEmail(email)
                .orElseThrow(()->new RuntimeErrorCodedException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
    }
    public UserInfo getUserById(Long id)
    {
        return userInfoRepo
                .findByUserId(id)
                .orElseThrow(()->new RuntimeErrorCodedException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
    }

}
