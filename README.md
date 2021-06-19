# Optumizers

## Contents

- [Submission or project name](#submission-or-project-name)
  - [Contents](#contents)
  - [Short description](#short-description)
    - [What's the problem?](#whats-the-problem)
    - [How can technology help?](#how-can-technology-help)
    - [The idea](#the-idea)
  - [Demo video](#demo-video)
  - [The architecture](#the-architecture)
  - [Live demo](#live-demo)
  - [Built with](#built-with)
  - [Contributing](#contributing)
  - [Versioning](#versioning)
  - [Authors](#authors)
  - [License](#license)
  - [Acknowledgments](#acknowledgments)

## Short description

### What's the problem?

Cars are spewing out up to 306,000 extra tons of carbon dioxide per year because their tires are under-inflated. These emissions directly contribute to increased greenhouse gases in the atmosphere. Reducing pollution by keeping ones tires at the proper pressure is a crucial step towards sustaining the fuel economy.

### How can technology help?

Technology can facilitate a solution to the lack of Tire Pressure Monitoring Systems in India. Data-driven Machine Learning and Image Recognition approaches can be used to analyze historical data and estimate the tire pressures in our vehicles.

### The idea

We present a new AI based mobile application that utilizes the mobile camera to capture, Open CV package to estimate the tire pressure and Machine Learning models to predict the percentage drop in fuel efficiency if the vehicle is driven with the current state of the tires. Our solution also recommends nearest gas stations and mechanics.


## Demo video

![Watch the video](https://github.com/Mrinalini97/Optumizers/blob/main/Thumbnaiil.png)(https://www.youtube.com/watch?v=Cd804RbV8kg)

## The architecture

![Video transcription/translation app](https://github.com/Mrinalini97/Optumizers/blob/main/architecture.png)

1. The user navigates to the app and uploads an image file.
2. Open CV processes the image and provides the tire radii measurement to Random Forest Classifier.
3. The Classification model is deployed on IBM Watson Machine Learning platform.
4. Flask API receives the predicted state of input tire image, and outputs the percentage drop in fuel efficiency.
5. The user can also access the Google Maps API and locate the nearest gas station or mechanics.

## Live demo

You can find a running system to test at [callforcode.mybluemix.net](http://callforcode.mybluemix.net/).

## Built with

- [IBM Watson Studio](https://www.ibm.com/in-en/cloud/watson-studio) - employs artificial intelligence and user-friendly tools to empower and streamline your data analytics.
- [Watson Machine Learning](https://www.ibm.com/in-en/cloud/machine-learning) - helps data scientists and developers accelerate AI and machine learning deployment on IBM Cloud Pak® for Data.

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

## Authors

## License

This project is licensed under the Apache 2 License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

