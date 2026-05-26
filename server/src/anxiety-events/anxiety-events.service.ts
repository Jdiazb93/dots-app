import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';

import { Model } from 'mongoose';

import {
  AnxietyEvent,
  AnxietyEventDocument,
} from './schemas/anxiety-event.schema';

import { CreateAnxietyEventDto } from './dto/create-anxiety-event.dto';

@Injectable()
export class AnxietyEventsService {
  constructor(
    @InjectModel(AnxietyEvent.name)
    private anxietyEventModel: Model<AnxietyEventDocument>,
  ) {}

  async createOrUpdate(createAnxietyEventDto: CreateAnxietyEventDto) {
    const { user, clicksCount, date } = createAnxietyEventDto;

    console.log(
      'Creating or updating anxiety event for user:',
      user,
      'on date:',
      date,
      'with clicksCount:',
      clicksCount,
    );

    return this.anxietyEventModel.findOneAndUpdate(
      {
        user,
        date,
      },
      {
        $inc: {
          clicksCount,
        },
      },
      {
        upsert: true,
        new: true,
      },
    );
  }

  async findByUser(user: string) {
    console.log('Finding anxiety events for user:', user);
    return this.anxietyEventModel.find({
      user,
    });
  }

  async findByUserAndDates(user: string, dates: string[]) {
    return this.anxietyEventModel.find({
      user,
      date: { $in: dates },
    });
  }
}
