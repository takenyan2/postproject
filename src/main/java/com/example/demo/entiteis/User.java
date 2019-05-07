package com.example.demo.entiteis;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @NotNull
    private  String userName;

    /**
     * GitHub Providerから取得したアクセストークン
     */
    @NotNull
    private String accessToken;

    /**
     * アプリ内で新しく発行したトークン
     */
    @NotNull
    private String appToken;

    @NotNull
    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * トークン有効期限チェック
     *
     * @param tokenExpiration 　トークンの有効期限
     * @return 有効期限切れ(true)
     */
    public boolean isExperired(long tokenExpiration) {
        LocalDateTime tokenCreatedAt = getCreatedAt();
        LocalDateTime now = LocalDateTime.now();
        return  tokenCreatedAt.plusMinutes(tokenExpiration).isBefore(now);

    }

}

