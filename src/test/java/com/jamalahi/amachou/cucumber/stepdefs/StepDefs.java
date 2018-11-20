package com.jamalahi.amachou.cucumber.stepdefs;

import com.jamalahi.amachou.AmachouApp;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.boot.test.context.SpringBootTest;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = AmachouApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
