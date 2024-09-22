package org.example.seeders;

import com.github.javafaker.Faker;
import org.example.model.CategoryEntity;
import org.example.model.ProductEntity;
import org.example.model.ProductImageEntity;
import org.example.repo.CategoryRepository;
import org.example.service.FileSaveFormat;
import org.example.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ProductSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final StorageService storageService;
    private final Faker faker = new Faker();
    public ProductSeeder(StorageService storageService,CategoryRepository categoryRepository) {
        this.storageService = storageService;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws IOException {
        if (categoryRepository.count() == 0) {
            int categoryCount = 3; //кільскість категорій
            //мінімальна і максимальна кількість фото для 1 товару
            int min = 3;
            int max = 5;
            int productsPerCategoryCount = 5;
            //Кількість усіх фото, які потрібно зробити
            int imageCount = categoryCount * (1 + (productsPerCategoryCount * max));
            ExecutorService executor = Executors.newFixedThreadPool(20);
            //список задач, які потрібно виконати
            List<CompletableFuture<String>> imagesFutures = new ArrayList<>();
            for (int i = 0; i < imageCount; i++) {
                imagesFutures.add(
                        CompletableFuture.supplyAsync(() -> {
                            try {
                                return storageService.saveImage("https://picsum.photos/1200/800", FileSaveFormat.WEBP);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }, executor)
                );
            }
            //Фото закачують з мережі і зберігають у папку
            // Очікуємо завершення всіх завантажень зображень
            CompletableFuture<Void> allImages = CompletableFuture.allOf(imagesFutures.toArray(new CompletableFuture[0]));

            // Після завершення завантаження всіх зображень
            allImages.thenRun(() -> {
                List<String> imagesUrls = imagesFutures.parallelStream()
                        .map(CompletableFuture::join)
                        .toList();
                executor.shutdown();
                List<CategoryEntity> categories = new ArrayList<>();
                int imageIndex = 0; //початкова позиція назви фото

                for (int i = 0; i < categoryCount; i++) {
                    // Створюємо нову категорію
                    CategoryEntity category = new CategoryEntity(
                            0,
                            faker.commerce().productName(),
                            imagesUrls.get(imageIndex++), //зберігаєммо фото для категорій - їхні імена
                            faker.lorem().sentence(10),
                            LocalDateTime.now(),
                            new ArrayList<>()
                    );

                    List<ProductEntity> products = new ArrayList<>();
                    for (int j = 0; j < productsPerCategoryCount; j++) {
                        // Створюємо новий продукт
                        ProductEntity product = new ProductEntity(
                                null,
                                faker.commerce().productName(),
                                faker.lorem().sentence(10),
                                LocalDateTime.now(),
                                faker.number().randomDouble(2, 10, 100),
                                faker.number().randomDouble(2, 0, 20),
                                category,
                                new ArrayList<>()
                        );


                        // Generate random integer between min (inclusive) and max (inclusive)
                        int randomMax = (int)(Math.random() * ((max - min) + 1)) + min;

                        List<ProductImageEntity> images = new ArrayList<>();
                        for (int k = 0; k < randomMax; k++) {
                            // Використовуємо наступне зображення для продукту
                            images.add(new ProductImageEntity(
                                    null,
                                    imagesUrls.get(imageIndex++),
                                    k,
                                    new Date(),
                                    false,
                                    product
                            ));
                        }

                        product.setProductImages(images);
                        products.add(product);
                    }

                    category.setProducts(products);
                    categories.add(category);
                }

                // Збереження категорій з продуктами і зображеннями в базу даних
                categoryRepository.saveAll(categories);
                System.out.println("Сид бази даних завершено!");
            }).exceptionally(ex -> {
                System.err.println("Помилка при збереженні категорій: " + ex.getMessage());
                return null;
            });
        }
    }
}