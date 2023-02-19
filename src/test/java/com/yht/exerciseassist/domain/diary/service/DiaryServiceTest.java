package com.yht.exerciseassist.domain.diary.service;

import com.yht.exerciseassist.ResponseResult;
import com.yht.exerciseassist.domain.DateTime;
import com.yht.exerciseassist.domain.diary.BodyPart;
import com.yht.exerciseassist.domain.diary.Diary;
import com.yht.exerciseassist.domain.diary.ExerciseInfo;
import com.yht.exerciseassist.domain.diary.dto.*;
import com.yht.exerciseassist.domain.diary.repository.DiaryRepository;
import com.yht.exerciseassist.domain.media.Media;
import com.yht.exerciseassist.domain.media.service.MediaService;
import com.yht.exerciseassist.domain.member.Member;
import com.yht.exerciseassist.domain.member.MemberType;
import com.yht.exerciseassist.domain.member.repository.MemberRepository;
import com.yht.exerciseassist.jwt.SecurityUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
@Transactional
@Rollback
@ActiveProfiles("test")
class DiaryServiceTest {

    private static MockedStatic<SecurityUtil> securityUtilMockedStatic;
    DiaryService diaryService;
    @MockBean
    private DiaryRepository diaryRepository;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private MediaService mediaService;

    @AfterEach
    public void afterAll() {
        securityUtilMockedStatic.close();
    }

    @BeforeEach
    void setUp() {
        diaryService = new DiaryService(diaryRepository, memberRepository, mediaService);
        securityUtilMockedStatic = mockStatic(SecurityUtil.class);
    }

    @Test
    public void saveDiary() throws IOException {
        //given
        ExerciseInfoDto exerciseInfoDto = new ExerciseInfoDto();
        exerciseInfoDto.setExerciseName("pushUp");
        exerciseInfoDto.setReps(10);
        exerciseInfoDto.setCardio(true);
        exerciseInfoDto.setExSetCount(10);
        exerciseInfoDto.setCardioTime(30);
        exerciseInfoDto.setBodyPart(BodyPart.TRICEP);
        exerciseInfoDto.setFinished(true);

        List<ExerciseInfoDto> exerciseInfoDtoList = new ArrayList<>();
        exerciseInfoDtoList.add(exerciseInfoDto);

        WriteDiaryDto writeDiaryDto = new WriteDiaryDto();
        writeDiaryDto.setExerciseInfo(exerciseInfoDtoList);
        writeDiaryDto.setReview("오늘 운동 끝");
        writeDiaryDto.setExerciseDate("2023-01-30");

        Member member = Member.builder()
                .username("username")
                .email("test@test.com")
                .loginId("testId3")
                .dateTime(new DateTime("2023-02-11 11:11", "2023-02-11 11:11", null))
                .role(MemberType.USER)
                .password("testPassword3!")
                .field("서울시")
                .build();

        given(SecurityUtil.getCurrentUsername()).willReturn("username");
        Mockito.when(memberRepository.findByUsername(SecurityUtil.getCurrentUsername())).thenReturn(Optional.ofNullable(member));

        ResponseResult responseResult = new ResponseResult(HttpStatus.CREATED.value(), "2023-01-30");

        String fileName = "tuxCoding.jpg";
        MockMultipartFile mediaFile = new MockMultipartFile("files", fileName, "image/jpeg", new FileInputStream("/Users/10000doo/Documents/wallpaper/" + fileName));///Users/10000doo/Documents/wallpaper/Users/jeong-yunju/Documents/wallpaper
        List<MultipartFile> mediaFileList = new ArrayList<>();
        mediaFileList.add(mediaFile);
        //when
        ResponseResult responseResult1 = diaryService.saveDiary(writeDiaryDto, mediaFileList);

        //then
        assertThat(responseResult1).isEqualTo(responseResult);
    }

    @Test
    public void getDiaryList() {
        //given
        given(SecurityUtil.getCurrentUsername()).willReturn("username");
        List<Diary> diaries = new ArrayList<>();

        Member member = Member.builder()
                .username("username")
                .email("test@test.com")
                .loginId("testId3")
                .dateTime(new DateTime("2023-02-11 11:11", "2023-02-11 11:11", null))
                .role(MemberType.USER)
                .password("testPassword3!")
                .field("서울시")
                .build();

        for (int i = 1; i < 21; i++) {

            ExerciseInfo exInfo = ExerciseInfo.builder()
                    .exerciseName("pushUp")
                    .reps(10)
                    .exSetCount(10)
                    .cardio(false)
                    .cardioTime(0)
                    .finished(true)
                    .build();

            ExerciseInfo exInfo2 = ExerciseInfo.builder()
                    .exerciseName("jogging")
                    .reps(0)
                    .exSetCount(0)
                    .cardio(true)
                    .cardioTime(30)
                    .finished(false)
                    .build();

            List<ExerciseInfo> exInfoList = new ArrayList<>();
            exInfoList.add(exInfo);
            exInfoList.add(exInfo2);

            Diary diary = Diary.builder()
                    .member(member)
                    .exerciseInfo(exInfoList)
                    .review("열심히 했다 오운완")
                    .exerciseDate("2023-01-" + i)
                    .dateTime(new DateTime("2023-02-11 11:11", "2023-02-11 11:11", null))
                    .build();

            diaries.add(diary);
        }

        List<Calender> calenderList = new ArrayList<>();

        for (int i = 1; i < 21; i++) {
            Calender calender = new Calender("2023-01-" + i, 50);
            calenderList.add(calender);
        }

        DiaryListDto diaryListDto = new DiaryListDto(calenderList, 50);

        Mockito.when(diaryRepository.findDiariesByUsername(SecurityUtil.getCurrentUsername(), "2023-01")).thenReturn(diaries);
        //when
        ResponseResult diaryList = diaryService.getDiaryList("2023-01");
        //then
        assertThat(diaryList.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(diaryList.getData()).isEqualTo(diaryListDto);
    }

    @Test
    public void getDiaryDetail() {
        //given
        given(SecurityUtil.getCurrentUsername()).willReturn("username");

        Member member = Member.builder()
                .username("username")
                .email("test@test.com")
                .loginId("testId3")
                .dateTime(new DateTime("2023-02-11 11:11", "2023-02-11 11:11", null))
                .role(MemberType.USER)
                .password("testPassword3!")
                .field("서울시")
                .build();

        ExerciseInfo exInfo = ExerciseInfo.builder()
                .exerciseName("pushUp")
                .reps(10)
                .exSetCount(10)
                .cardio(false)
                .cardioTime(0)
                .bodyPart(BodyPart.TRICEP)
                .finished(true)
                .build();

        List<ExerciseInfo> exInfoList = new ArrayList<>();
        exInfoList.add(exInfo);


        Media media = Media.builder()
                .originalFilename("test1.png")
                .filename("c42d3bb9-10de-45f6-8533-14b3dac07e4e.png")
                .filePath("/Users/jeong-yunju/Project/Capstone/20230215/YouHaveTo/src/main/resources/media/c42d3bb9-10de-45f6-8533-14b3dac07e4e.png")
                .post(null)
                .dateTime(new DateTime("2023-02-11 11:11", "2023-02-11 11:11", null))
                .build();
        media.setMediaIdUsedOnlyTest(1L);


        List<Media> mediaId = new ArrayList<>();
        mediaId.add(media);

        Diary diaryDetail = Diary.builder()
                .member(member)
                .exerciseInfo(exInfoList)
                .review("열심히 했다. 오운완")
                .exerciseDate("2023-01-20")
                .dateTime(new DateTime("2023-02-11 11:11", "2023-02-11 11:11", null))
                .build();

        diaryDetail.setDiaryIdUsedOnlyTest(1L);

        diaryDetail.linkToMedia(mediaId);

        Optional<Diary> opDiaryDetail = Optional.of(diaryDetail); //Repository에서  찾아올 다이어리
////////////////////////////////////////////////////////////////
        ExerciseInfoDto exerciseInfoDto = new ExerciseInfoDto(); //내가 기대한 Dto반환값
        exerciseInfoDto.setExerciseName("pushUp");
        exerciseInfoDto.setReps(10);
        exerciseInfoDto.setCardio(false);
        exerciseInfoDto.setExSetCount(10);
        exerciseInfoDto.setCardioTime(0);
        exerciseInfoDto.setBodyPart(BodyPart.TRICEP);
        exerciseInfoDto.setFinished(true);

        List<ExerciseInfoDto> exerciseInfoDtoList = new ArrayList<>();
        exerciseInfoDtoList.add(exerciseInfoDto);

        List<String> mediaIdList = new ArrayList<>();
        mediaIdList.add("null" + "/media/" + 1);

        DiaryDetailDto diaryDetailDto = DiaryDetailDto.builder()
                .exerciseDate("2023-01-20")
                .review("열심히 했다. 오운완")
                .exerciseInfo(exerciseInfoDtoList)
                .dateTime(new DateTime("2023-02-11 11:11", "2023-02-11 11:11", null))
                .mediaList(mediaIdList)
                .build();

        //이 정보가 맞다면 Optional<Diary>타입의 diartdetail을 반환해줘
        Mockito.when(diaryRepository.findDiaryDetailsByUsername(SecurityUtil.getCurrentUsername(), "2023-01-20")).thenReturn(opDiaryDetail);
        //when
        ResponseResult diary = diaryService.getdiaryDetail("2023-01-20");
        //then
        assertThat(diary.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(diary.getData()).isEqualTo(diaryDetailDto);

    }


}