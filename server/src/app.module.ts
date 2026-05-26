import { Module } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { MongooseModule } from '@nestjs/mongoose';
import { AnxietyEventsModule } from './anxiety-events/anxiety-events.module';

@Module({
  imports: [
    // ENV
    ConfigModule.forRoot({
      isGlobal: true,
    }),

    // MONGO
    MongooseModule.forRootAsync({
      inject: [ConfigService],

      useFactory: (configService: ConfigService) => ({
        uri: configService.get<string>('MONGO_URI'),
      }),
    }),

    AnxietyEventsModule,
  ],
})
export class AppModule {}
