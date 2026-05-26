import { Body, Controller, Get, Param, Post } from '@nestjs/common';

import { AnxietyEventsService } from './anxiety-events.service';

import { CreateAnxietyEventDto } from './dto/create-anxiety-event.dto';

@Controller('anxiety-events')
export class AnxietyEventsController {
  constructor(private readonly anxietyEventsService: AnxietyEventsService) {}

  @Post()
  create(
    @Body()
    createAnxietyEventDto: CreateAnxietyEventDto,
  ) {
    return this.anxietyEventsService.createOrUpdate(createAnxietyEventDto);
  }

  @Get(':user')
  findByUser(@Param('user') user: string) {
    const today = new Date();

    const last7Days: string[] = [];

    for (let i = 0; i < 7; i++) {
      const d = new Date();
      d.setDate(today.getDate() - i);

      last7Days.push(
        d.toISOString().split('T')[0], // yyyy-MM-dd
      );
    }

    return this.anxietyEventsService.findByUserAndDates(user, last7Days);
  }
}
