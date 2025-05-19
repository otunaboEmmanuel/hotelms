package com.aiproject.ics.repository.jpa;

import com.aiproject.ics.entity.Otp;
import com.aiproject.ics.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<Otp,Integer> {

    //@Query("select fp from Otp fp where fp.otp =?1 and fp.user=?2")
    Optional<Otp> findByOtpAndUser(Integer otp, Users user);
}
