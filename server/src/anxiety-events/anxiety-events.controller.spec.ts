import { Test, TestingModule } from '@nestjs/testing';
import { AnxietyEventsController } from './anxiety-events.controller';
import { AnxietyEventsService } from './anxiety-events.service';

describe('AnxietyEventsController', () => {
  let controller: AnxietyEventsController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [AnxietyEventsController],
      providers: [AnxietyEventsService],
    }).compile();

    controller = module.get<AnxietyEventsController>(AnxietyEventsController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
