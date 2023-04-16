import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Person {
    private String name;
    private Integer age;

    @Override
    public boolean equals(Object obj) {
        Person other = (Person) obj;

        return this.name.equals(other.getName()) && this.age.equals(other.getAge());
    }

    @Override
    public int hashCode() {
        return this.name.hashCode()+this.age*10;
    }
}
