package com.utn.API_CentroDeportivo.service;

public interface IEmailService {

    void sendEmail(String to, String subject, String htmlContent);

}