package com.project.vetdata.repository;

import com.project.vetdata.model.DogBreed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DogBreedRepository extends JpaRepository<DogBreed, Long> {

    Optional<DogBreed> findByIdExternalApi(String idExternalApi);

    /**
     * O countQuery é a consulta que retorna o total de registros encontrados sem aplicar paginação.
     * Ele é obrigatório quando você usa @Query(...) com paginação (Pageable) e
     * está escrevendo uma consulta nativa (nativeQuery = true).
     *
     * Quando você retorna uma Page<DogBreed>, o Spring Data precisa de duas coisas:
     *
     * Os dados da página atual → obtidos com a query principal (value)
     *
     * O número total de registros que atendem ao filtro (para calcular o total de páginas, exibir "Exibindo 1 a 10 de 53 registros", etc.)
     *
     * Sem o countQuery, o Spring não consegue saber o total de registros filtrados, e a paginação no front (como o DataTables) fica quebrada.
     * */
    @Query(value = "SELECT * FROM dog_breeds WHERE LOWER(name) LIKE CONCAT('%', LOWER(:name), '%')",
            countQuery = "SELECT COUNT(*) FROM dog_breeds WHERE LOWER(name) LIKE CONCAT('%', LOWER(:name), '%')",
            nativeQuery = true)
    Page<DogBreed> searchByName(@Param("name") String name, Pageable pageable);
}