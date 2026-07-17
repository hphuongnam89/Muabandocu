package com.cho2hand.marketplace.service.impl.user;

import com.cho2hand.marketplace.dto.user.CreateUserRequest;
import com.cho2hand.marketplace.dto.user.UpdateUserRequest;
import com.cho2hand.marketplace.dto.user.UserResponse;
import com.cho2hand.marketplace.exception.UserNotFoundException;
import com.cho2hand.marketplace.mapper.user.UserMapper;
import com.cho2hand.marketplace.repository.user.UserRepository;
import com.cho2hand.marketplace.service.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        return userMapper.toResponse(userRepository.save(userMapper.toEntity(request)));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return userMapper.toResponse(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }

    @Override
    public UserResponse update(Long id, UpdateUserRequest request) {
        var user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userMapper.update(user, request);
        return userMapper.toResponse(user);
    }
}
