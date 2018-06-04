package com.erlangshen.sdk;

public class Test {
    public static void main(String[] args)  throws Exception {
        ElsClient client = new ElsClient("2VudOfXnTf6oXKSEOJgnaw", "MTRkNjMwZmRmODU3NDllNmFmNTY3ODk3Yzk1NTcyMTBzazE1MjM2MDgzMTI2Njg");
        System.out.print(client.sendMail("register", "xiaoyao495@163.com", "", "", null));
    }
}
