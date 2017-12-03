package server.model;


import common.AccessPermission;
import common.ReadWritePermission;

import java.io.Serializable;
import javax.persistence.*;

@NamedQueries(value = {
        @NamedQuery(
                name = "findFileByName",
                query = "SELECT file FROM File file WHERE file.name LIKE :filename"
        ),
        @NamedQuery(
                name = "deleteFileByName",
                query = "DELETE FROM File file WHERE file.name LIKE :filename"
        ),
        @NamedQuery(
                name = "getAllFiles",
                query = "SELECT file FROM File file"
        ),
        @NamedQuery(
                name = "getUserFiles",
                query = "SELECT file FROM File file WHERE file.owner.userID = :userID OR file.accessPermission = common.AccessPermission.PUBLIC"
        )
})

/**
 * Each File must have (name, size, owner, public/private access permission,
 write/read permissions).
 */
@Entity(name="File")
public class File implements Serializable {
    @Id
    private String name;

    private int size;

    @Enumerated(EnumType.STRING)
    private AccessPermission accessPermission;

    @Enumerated(EnumType.STRING)
    private ReadWritePermission readWritePermission;

    @ManyToOne
    @JoinColumn(name="owner",nullable = false)
    private Person owner;

    private boolean notify;

    public File() {
    }

    public File(String name, int size, Person owner, AccessPermission accessPermission, ReadWritePermission readWritePermission ) {
        this.name = name;
        this.size = size;
        this.owner = owner;
        this.accessPermission = accessPermission;
        this.readWritePermission =readWritePermission;
        this.notify=false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public AccessPermission getAccessPermission() {
        return accessPermission;
    }

    public void setAccessPermission(AccessPermission accessPermission) {
        this.accessPermission = accessPermission;
    }

    public ReadWritePermission getReadWritePermission() {
        return readWritePermission;
    }

    public void setReadWritePermission(ReadWritePermission readWritePermission) {
        this.readWritePermission = readWritePermission;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("File: [");
        string.append("name: ");
        string.append(name);
        string.append(", size: ");
        string.append(size);
        string.append(", owner: ");
        string.append(owner.getUsername());
        string.append(", accessPermission: ");
        string.append(accessPermission);
        string.append(", readWritePermission: ");
        string.append(readWritePermission);
        string.append("]");
        return string.toString();
    }
}
