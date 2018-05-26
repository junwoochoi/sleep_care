package com.example.dell.sleepcare.Model;

import java.util.ArrayList;

public class PSQIScore {
    ArrayList<String> answers;

    public PSQIScore(ArrayList<String> answers) {
        this.answers = answers;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }

    public int getComponent1(){
        switch (answers.get(14)){
            case "1001": return 0;
            case "1002": return 1;
            case "1003": return 2;
            case "1004": return 3;
        }
        return -1;
    }

    public int getComponent2(){
        int answer2 = Integer.parseInt(answers.get(1));
        int answer5a = Integer.parseInt(answers.get(4));
        int score=-1;
        if(answer2<16){
            score = 0;
        } else if(answer2<31){
            score = 1;
        } else if(answer2<60){
            score = 2;
        } else if(answer2>=60){
            score = 3;
        }


        switch (answer5a){
            case 1001: score+= 0; break;
            case 1002: score+= 1; break;
            case 1003: score+= 2; break;
            case 1004: score+= 3; break;
        }

        if(score==0){
            return 0;
        } else if(score<3){
            return 1;
        } else if(score<5){
            return 2;
        } else if(score<7){
            return 3;
        }

        return -1;
    }

    public int getComponent3(){
        float answer4 = Float.parseFloat(answers.get(3));
        if(answer4<=5){
            return 3;
        } else if(answer4<6){
            return 2;
        } else if(answer4<7){
            return 1;
        } else if(answer4>=7){
            return 0;
        }
        return -1;
    }

    public int getComponent4(){
        float sleepTime = Float.parseFloat(answers.get(3));
        float wakeupTime = Float.parseFloat(answers.get(2));
        float bedTime = Float.parseFloat(answers.get(0));


        float percentage = sleepTime/(wakeupTime-bedTime)*100;


        if(percentage>=85){
            return 0;
        } else if(percentage>=75){
            return 1;
        } else if(percentage>=65){
            return 2;
        } else if(percentage<65){
            return 3;
        }

        return -1;
    }

    public int getComponent5(){
        int score = 0;
        for(int i=1; i<10; i++){
            int answer = Integer.parseInt(answers.get(i+4));
            switch (answer){
                case 1001: score+= 0; break;
                case 1002: score+= 1; break;
                case 1003: score+= 2; break;
                case 1004: score+= 3; break;
            }
        }
        if(score==0){
            return 0;
        } else if(score<10){
            return 1;
        } else if(score<19){
            return 2;
        } else if(score<28){
            return 3;
        }
        return -1;
    }
    public int getComponent6(){
        switch (answers.get(15)){
            case "1001": return 0;
            case "1002": return 1;
            case "1003": return 2;
            case "1004": return 3;
        }
        return -1;
    }

    public int getComponent7(){
        int answer8 = Integer.parseInt(answers.get(16));
        int answer9 = Integer.parseInt(answers.get(17));
        int score=-1;
        switch (answer8){
            case 1001: score= 0;
            case 1002: score= 1;
            case 1003: score= 2;
            case 1004: score= 3;
        }
        switch (answer9){
            case 1001: score+= 0;
            case 1002: score+= 1;
            case 1003: score+= 2;
            case 1004: score+= 3;
        }

        if(score==0){
            return 0;
        } else if(score<3){
            return 1;
        } else if(score<5){
            return 2;
        } else if(score<7){
            return 3;
        } else if(score<0){
            return -1;
        }
        return -1;
    }

    public int getComponent(){
        return getComponent1()+getComponent2()+getComponent3()+getComponent4()+getComponent5()+getComponent6()+getComponent7();
    }
}
