package com.jaywant.demo.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jaywant.demo.Entity.Certificate;

public interface CertificateRepo extends JpaRepository<Certificate, Long> {

}
