import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { AnxietyEventsService } from './anxiety-events.service';
import { AnxietyEventsController } from './anxiety-events.controller';

import {
  AnxietyEvent,
  AnxietyEventSchema,
} from './schemas/anxiety-event.schema';

@Module({
  imports: [
    MongooseModule.forFeature([
      {
        name: AnxietyEvent.name,
        schema: AnxietyEventSchema,
      },
    ]),
  ],

  controllers: [AnxietyEventsController],

  providers: [AnxietyEventsService],
})
export class AnxietyEventsModule {}
