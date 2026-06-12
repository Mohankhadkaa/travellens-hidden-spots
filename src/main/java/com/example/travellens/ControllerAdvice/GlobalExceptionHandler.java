package com.example.travellens.controlleradvice;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleMultipart(MultipartException ex, HttpServletRequest request) {
        log.error("Multipart error for {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", 400);
        if (ex instanceof MaxUploadSizeExceededException) {
            mav.addObject("message", "Uploaded file exceeds the maximum allowed size (5 MB).");
        } else if (ex.getMessage() != null && ex.getMessage().contains("multipart")) {
            mav.addObject("message", "Invalid form submission. Please ensure the form's enctype is set to multipart/form-data.");
        } else {
            mav.addObject("message", "File upload failed. Please try again.");
        }
        return mav;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleMissingParam(MissingServletRequestParameterException ex) {
        log.error("Missing request parameter: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", 400);
        mav.addObject("message", "A required form field is missing: " + ex.getParameterName());
        return mav;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNotFound(NoResourceFoundException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", 404);
        mav.addObject("message", "The page you are looking for does not exist.");
        return mav;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleAll(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception for {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", 500);
        mav.addObject("message", "An unexpected error occurred. Please try again or contact support.");
        return mav;
    }
}
