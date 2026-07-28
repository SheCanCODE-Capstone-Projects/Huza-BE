package com.huza.huzabackend.Mapper;


import com.huza.huzabackend.dto.CreateSkillRequest;
import com.huza.huzabackend.dto.SkillResponse;
import com.huza.huzabackend.entity.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillMapper {

    SkillResponse toResponse(Skill skill);

    List<SkillResponse> toResponseList(List<Skill> skills);

    @Mapping(target = "id", ignore = true)
    Skill toEntity(CreateSkillRequest request);
}