package com.ljseokd.dongari.modules.account;

import lombok.*;

import javax.persistence.*;

import java.time.LocalDateTime;

import static lombok.AccessLevel.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = PROTECTED)
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private String bio;

    private String url;

    private String occupation;

    private String location;

    @Lob
    private String profileImage;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    private LocalDateTime joinedAt;


}
