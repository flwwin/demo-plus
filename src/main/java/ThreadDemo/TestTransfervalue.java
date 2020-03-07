package ThreadDemo;

/**
 * 值传递还是引用传递的问题
 * 可以这么说吧：基本数据类型是值传递，引用数据类型是引用传递
 * 但是官方文档：java只有引用传递
 */

class Person {
    private int age;
    private String personName;

    public Person(int age) {
        this.age = age;
    }

    public Person(int age, String personName) {
        this.age = age;
        this.personName = personName;
    }

    public Person(String personName) {
        this.personName = personName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }
}

public class TestTransfervalue {
    public void changeValue1(int age) {
        age = 30;

    }

    public void changevalue2(Person person) {
        person.setPersonName(" xxx");
    }

    public void changeValue3(String str) {
        str = "xx";
    }

    public static void main(String[] args) {
        TestTransfervalue test = new TestTransfervalue();
        int age = 20;
        test.changeValue1(age);
        System.out.println("age----" + age);
        Person person = new Person("abc");
        test.changevalue2(person);
        System.out.println("personNm-----" + person.getPersonName());
        String str = " abc";
        test.changeValue3(str);
        System.out.println("string----" + str);
    }

}
