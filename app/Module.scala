import com.google.inject.AbstractModule
import java.time.Clock

import repository._
import service._

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule {

  override def configure() = {
    // Use the system clock as the default implementation of Clock
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
    // Ask Guice to create an instance of ApplicationTimer when the
    // application starts.
    bind(classOf[ApplicationTimer]).asEagerSingleton()
    // Set AtomicCounter as the implementation for Counter.
    bind(classOf[Counter]).to(classOf[AtomicCounter])

    bind(classOf[ConfigurationRepository]).to(classOf[ConfigurationRepositoryImpl])
    bind(classOf[TransactionRepository]).to(classOf[TransactionRepositoryImpl])
    bind(classOf[BankRepository]).to(classOf[BankRepositoryImpl])
    bind(classOf[MerchantRepository]).to(classOf[MerchantRepositoryImpl])
    bind(classOf[EstablishmentRepository]).to(classOf[EstablishmentRepositoryImpl])
    bind(classOf[BankAgreementRepository]).to(classOf[BankAgreementRepositoryImpl])
    bind(classOf[BankAgreementService]).to(classOf[BankAgreementServiceImpl])
    bind(classOf[EstablishmentService]).to(classOf[EstablishmentServiceImpl])
    bind(classOf[EstablishmentBankAgreementRepository]).to(classOf[EstablishmentBankAgreementRepositoryImpl])
    bind(classOf[MerchantEstablishmentRepository]).to(classOf[MerchantEstablishmentRepositoryImpl])
    bind(classOf[NormalizedStatusRepository]).to(classOf[NormalizedStatusRepositoryImpl])
    bind(classOf[BankResponseStatusRepository]).to(classOf[BankResponseStatusRepositoryImpl])

    bind(classOf[TransactionService]).to(classOf[TransactionServiceImpl])
    bind(classOf[PaymentService]).to(classOf[PaymentServiceImpl])
    bind(classOf[PaymentRepository]).to(classOf[PaymentRepositoryImpl])
  }

}
