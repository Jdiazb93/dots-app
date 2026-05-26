import { Test, TestingModule } from '@nestjs/testing';
import { AnxietyEventsService } from './anxiety-events.service';

describe('AnxietyEventsService', () => {
  let service: AnxietyEventsService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [AnxietyEventsService],
    }).compile();

    service = module.get<AnxietyEventsService>(AnxietyEventsService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
