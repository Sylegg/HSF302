package fa25.beitweb.service;

import fa25.beitweb.entity.Room;


import java.util.List;

public interface RoomService {
    public void createRoom(Room room);
    public List<Room> getAllRooms();

    public Room getRoomById(long id);
    public void deleteRoomById(long id);
    public void updateRoom(Room room);
}
