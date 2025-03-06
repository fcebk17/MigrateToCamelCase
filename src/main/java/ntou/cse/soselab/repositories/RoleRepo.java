package ntou.cse.soselab.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ntou.cse.soselab.entites.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {

}
