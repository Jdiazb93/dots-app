import { PartialType } from '@nestjs/mapped-types';
import { CreateAnxietyEventDto } from './create-anxiety-event.dto';

export class UpdateAnxietyEventDto extends PartialType(CreateAnxietyEventDto) {}
