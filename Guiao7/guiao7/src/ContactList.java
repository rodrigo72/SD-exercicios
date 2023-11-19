import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ContactList extends ArrayList<Contact> {

    private List<Contact> contacts;
    public ContactList() {
        contacts = new ArrayList<>();

        contacts.add(new Contact("Rodrigo", 20, 123123123, null,
                new ArrayList<>(List.of("rodrigo@proton.me"))));
        contacts.add(new Contact("Artur", 32, 123123123, "CC",
                new ArrayList<>(List.of("artur@gmail.com", "artur@uminho.com"))));
        contacts.add(new Contact("sim", 32, 123123123, "CC",
                new ArrayList<>(List.of("sim@gmail.com", ".sim@uminho.com"))));
    }

    // @TODO
    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(contacts.size());
        for (Contact c : contacts) {
            c.serialize(out);
        }
    }

    // @TODO
    public static ContactList deserialize(DataInputStream in) throws IOException {
        ContactList cl = new ContactList();
        int nContacts = in.readInt();
        for (int i = 0; i < nContacts; i++) {
            cl.add(Contact.deserialize(in));
        }
        return cl;
    }

}
