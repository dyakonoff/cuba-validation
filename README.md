# Input data validation in [CUBA platform](https://www.cuba-platform.com/)

## Introduction

Input validation is one of common tasks for everyday developer’s life. We need to check our data in many different situations: after getting data from UI, from API calls, before saving your model to the DB etc, etc

In this article I want to touch the main approaches of data validation that CUBA.platform offers.

Here are the approaches I’d like to discuss:
1. [Bean validation that CUBA Studio offers for entities.](simple-validation/)
1. [Validation with custom annotations.](validation-with-custom-annotations/)
1. [Defining custom Validator class and groovy scripts for UI components.](validator-component/)
1. [Validation in UI screen controllers.](validation-in-controllers/)
1. [Using Entity listeners for validation.](listeners-validation)
1. [Using Transaction listeners to validate your data model.](listeners-validation)

## [Bean Validation](simple-validation/)

This is, without any doubt, the first type of validation that new users of the platform can see in [CUBA studio IDE.](https://www.cuba-platform.com/download)

![Figure 1: Standard entity validators in CUBA studio](resources/figure_1.png)

The studio gives users an easy way to annotate entity fields with most common validators.
