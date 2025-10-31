package fa25.beitweb.service;

import fa25.beitweb.entity.Room;
import fa25.beitweb.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Override
    public void createRoom(Room room) {
        roomRepository.save(room);
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }


    @Override
    public Room getRoomById(long id) {
        return roomRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteRoomById(long id) {
        roomRepository.deleteById(id);
    }

    @Override
    public void updateRoom(Room room) {
        Room existingRoom = roomRepository.findById(room.getRoomId()).orElse(null);
        if (existingRoom != null) {
            roomRepository.save(room);
        }
    }
}
