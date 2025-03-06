package ntou.cse.soselab.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ntou.cse.soselab.entites.Payment;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long>{

}
